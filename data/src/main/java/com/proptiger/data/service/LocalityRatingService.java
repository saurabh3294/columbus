package com.proptiger.data.service;

import java.lang.reflect.InvocationTargetException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;

import javax.annotation.PostConstruct;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.util.concurrent.Striped;
import com.proptiger.data.init.ExclusionAwareBeanUtilsBean;
import com.proptiger.data.model.LocalityRatings;
import com.proptiger.data.model.LocalityRatings.LocalityAverageRatingByCategory;
import com.proptiger.data.model.LocalityRatings.LocalityRatingDetails;
import com.proptiger.data.model.LocalityRatings.LocalityRatingUserCount;
import com.proptiger.data.repo.LocalityRatingDao;
import com.proptiger.data.util.Constants;
import com.proptiger.data.util.PropertyReader;
import com.proptiger.exception.ConstraintViolationException;
import com.proptiger.exception.ProAPIException;

/**
 * Service class to provide CRUD operations over locality ratings
 * 
 * @author Rajeev Pandey
 * 
 */
@Service
public class LocalityRatingService {

    private static Logger     logger = LoggerFactory.getLogger(LocalityRatingService.class);
    @Autowired
    private LocalityRatingDao localityRatingDao;
    
    @Autowired
    protected PropertyReader    propertyReader;
    
    private Striped<Lock>       locks;

    @PostConstruct
    private void init() {
        locks = Striped.lock(propertyReader.getRequiredPropertyAsType("image.lock.stripes.count", Integer.class));
    }

    /**
     * This method will return the distribution of rating by their total users,
     * average rating.
     * 
     * @param localityId
     * @return Map<String, Object> The information contained is as: 1:
     *         TOTAL_USERS_BY_RATING => Map<Double, Long> Here Double will be
     *         rating and Long will be number of users. 2: AVERAGE_RATING => The
     *         average rating of locality. 3: TOTAL_RATINGS => total rating
     *         users.
     */
    @Cacheable(value = Constants.CacheName.LOCALITY_RATING_USERS_COUNT_BY_RATING, key = "#localityId")
    public LocalityRatingDetails getUsersCountByRatingOfLocality(int localityId) {
        logger.debug("Get locality rating details for locality id {}", localityId);
        LocalityRatingDetails localityRatingDetails = new LocalityRatingDetails();
        List<LocalityRatingUserCount> ratingWiseUserCountList = localityRatingDao.getTotalUsersByRating(localityId);
        if (ratingWiseUserCountList == null || ratingWiseUserCountList.size() < 1) {
            return localityRatingDetails;
        }
        Map<Double, Long> ratingMap = new LinkedHashMap<>();

        double totalRating = 0;
        long totalUserCount = 0;

        for (LocalityRatingUserCount ratingUserCount : ratingWiseUserCountList) {
            double rating = ratingUserCount.getRating();
            long users = ratingUserCount.getUserCount();
            totalRating += rating * users;
            totalUserCount += users;
            ratingMap.put(rating, users);
        }
        if (totalUserCount < 1) {
            totalUserCount = 1;
        }
        double avgRating = totalRating / totalUserCount;

        localityRatingDetails = new LocalityRatingDetails(ratingMap, avgRating, totalUserCount);
        return localityRatingDetails;
    }

    /**
     * Computing average rating of all amenities of locality. Excluding null and
     * zero values while calculating average.
     * 
     * @param localityId
     * @return
     */
    @Cacheable(value = Constants.CacheName.LOCALITY_RATING_AVG_BY_CATEGORY, key = "#localityId")
    public LocalityAverageRatingByCategory getAvgRatingsOfLocalityByCategory(Integer localityId) {
        logger.debug("Get locality average rating of category for locality {}", localityId);
        LocalityAverageRatingByCategory avgRatingOfAmenities = localityRatingDao
                .getAvgRatingOfAmenitiesForLocality(localityId);
        return avgRatingOfAmenities;
    }

    /**
     * Computing average rating of all amenities of suburb. Excluding null and
     * zero values while calculating average.
     * 
     * @param suburbId
     * @return
     */
    public LocalityAverageRatingByCategory getAvgRatingsOfSuburbByCategory(Integer suburbId) {
        logger.debug("Get suburb average rating of category for suburb {}", suburbId);
        LocalityAverageRatingByCategory avgRatingOfAmenities = localityRatingDao
                .getAvgRatingOfAmenitiesForSuburb(suburbId);
        return avgRatingOfAmenities;
    }

    @CacheEvict(value = {
            Constants.CacheName.LOCALITY_RATING_AVG_BY_CATEGORY,
            Constants.CacheName.LOCALITY_RATING_USERS_COUNT_BY_RATING,
            Constants.CacheName.LOCALITY_RATING_USERS }, key = "#localityId")
    @Transactional(rollbackFor = { ConstraintViolationException.class })
    public LocalityRatings createLocalityRating(Integer userId, Integer localityId, LocalityRatings localityRatings) {
        // TODO in case of multiple request from same user and same locality
        // this method creates two row in database
        // TODO need to prevent this
        logger.debug("create locality rating for user {} locality {}", userId, localityId);
        LocalityRatings created = null;
        localityRatings.setLocalityId(localityId);
        /*
         * if non logged in user or unregistered user is trying to create rating
         * then making user id 0
         */
        if (userId == null) {
            localityRatings.setUserId(0);
            localityRatings.setReviewId(null);
            created = localityRatingDao.save(localityRatings);
        }
        else {
            // Creating lock-key based on userId and LocalityId
            Lock lock = locks.get(userId + "" + localityId);
            try {
                lock.lock();
                // find if rating already exist for this user and locality then
                // update that
                LocalityRatings ratingPresent = localityRatingDao.findByUserIdAndLocalityId(userId, localityId);
                if (ratingPresent != null) {
                    // update already existing ratings
                    BeanUtilsBean beanUtilsBean = new ExclusionAwareBeanUtilsBean();
                    try {
                        beanUtilsBean.copyProperties(ratingPresent, localityRatings);
                    }
                    catch (IllegalAccessException | InvocationTargetException e) {
                        throw new ProAPIException("locality review update failed", e);
                    }
                    created = ratingPresent;
                }
                else {
                    // creating new rating by user for locality
                    localityRatings.setUserId(userId);
                    created = localityRatingDao.save(localityRatings);
                }
            }
            finally {
                lock.unlock();
            }
        }
        return created;
    }

    /**
     * Get locality rating details for particular locality of user
     * 
     * @param userId
     * @param localityId
     * @return
     */
    @Cacheable(value = Constants.CacheName.LOCALITY_RATING_USERS, key = "#localityId")
    public LocalityRatings getLocalityRatingOfUser(Integer userId, Integer localityId) {
        LocalityRatings localityRating = localityRatingDao.findByUserIdAndLocalityId(userId, localityId);
        return localityRating;
    }

    //setting Half of the rating for backward compatibility as it will be based out of 10 now.
    public void updateRatingsByHalf(LocalityRatings rating) {
        if (rating != null ) {
            if (rating.getOverallRating() != null){
                rating.setOverallRating(rating.getOverallRating()/2);
            }
            if(rating.getLocation() != null) {
                rating.setLocation(rating.getLocation()/2);
            }
            if (rating.getSafety() != null) {
                rating.setSafety(rating.getSafety()/2);
            }
            if (rating.getTraffic() != null) {
                rating.setTraffic(rating.getTraffic()/2);
            }
            if (rating.getPubTrans() != null) {
                rating.setPubTrans(rating.getPubTrans()/2);
            }
            if (rating.getRestShop() != null) {
                rating.setRestShop(rating.getRestShop()/2);
            }
            if (rating.getSchools() != null) {
                rating.setSchools(rating.getSchools()/2);
            }
            if (rating.getSchools() != null) {
                rating.setHospitals(rating.getHospitals()/2);
            }
            if (rating.getCivic() != null) {
                rating.setCivic(rating.getCivic()/2);
            }
            if (rating.getParks() != 0) {
                rating.setParks(rating.getParks()/2);
            }
        }
    }

    public void updateRatingsByTwice(LocalityRatings createdRating) {
        if (createdRating != null) {
            if (createdRating.getOverallRating() != null) {
                createdRating.setOverallRating(createdRating.getOverallRating() * 2);
            }
            if (createdRating.getLocation() != null) {
                createdRating.setLocation(createdRating.getLocation() * 2);
            }
            if (createdRating.getSafety() != null) {
                createdRating.setSafety(createdRating.getSafety() * 2);
            }
            if (createdRating.getTraffic() != null) {
                createdRating.setTraffic(createdRating.getTraffic() * 2);
            }
            if (createdRating.getPubTrans() != null) {
                createdRating.setPubTrans(createdRating.getPubTrans() * 2);
            }
            if (createdRating.getRestShop() != null) {
                createdRating.setRestShop(createdRating.getRestShop() * 2);
            }
            if (createdRating.getSchools() != null) {
                createdRating.setSchools(createdRating.getSchools() * 2);
            }
            if (createdRating.getSchools() != null) {
                createdRating.setHospitals(createdRating.getHospitals() * 2);
            }
            if (createdRating.getCivic() != null) {
                createdRating.setCivic(createdRating.getCivic() * 2);
            }
            if (createdRating.getParks() != 0) {
                createdRating.setParks(createdRating.getParks() * 2);
            }
        }
    }
}
