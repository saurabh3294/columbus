package com.proptiger.data.service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.proptiger.data.model.LocalityRatings;
import com.proptiger.data.model.LocalityRatings.LocalityAverageRatingByCategory;
import com.proptiger.data.model.LocalityRatings.LocalityRatingDetails;
import com.proptiger.data.model.LocalityRatings.LocalityRatingUserCount;
import com.proptiger.data.repo.LocalityRatingDao;
import com.proptiger.data.util.Constants;
import com.proptiger.exception.ConstraintViolationException;

/**
 * Service class to provide CRUD operations over locality ratings
 * @author Rajeev Pandey
 * 
 */
@Service
public class LocalityRatingService {

	private static Logger logger = LoggerFactory
			.getLogger(LocalityRatingService.class);
	@Autowired
	private LocalityRatingDao localityRatingDao;
	
	/**
	 * This method will return the distribution of rating by their total users, average rating.
	 * @param localityId
	 * @return Map<String, Object> The information contained is as:
	 *         1: TOTAL_USERS_BY_RATING => Map<Double, Long> Here Double will be rating and Long 
	 *                                     will be number of users.
	 *         2: AVERAGE_RATING => The average rating of locality.
	 *         3: TOTAL_RATINGS => total rating users.        
	 */
	@Cacheable(value = Constants.CacheName.LOCALITY_RATING_USERS_COUNT_BY_RATING, key = "#localityId")
	public LocalityRatingDetails getUsersCountByRatingOfLocality(int localityId) {
		logger.debug("Get locality rating details for locality id {}",localityId);
		LocalityRatingDetails localityRatingDetails = new LocalityRatingDetails();
		List<LocalityRatingUserCount> ratingWiseUserCountList = localityRatingDao
				.getTotalUsersByRating(localityId);
		if (ratingWiseUserCountList == null
				|| ratingWiseUserCountList.size() < 1) {
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

		localityRatingDetails = new LocalityRatingDetails(
				ratingMap, avgRating, totalUserCount);
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
	public LocalityAverageRatingByCategory getAvgRatingsOfLocalityByCategory(Integer localityId){
		logger.debug("Get locality average rating of category for locality {}",localityId);
		LocalityAverageRatingByCategory avgRatingOfAmenities = localityRatingDao.getAvgRatingOfAmenitiesForLocality(localityId);
		return avgRatingOfAmenities;
	}
	
	
	@CacheEvict(value = { Constants.CacheName.LOCALITY_RATING_AVG_BY_CATEGORY,
			Constants.CacheName.LOCALITY_RATING_USERS_COUNT_BY_RATING }, key = "#localityId")
	@Transactional(rollbackFor = {ConstraintViolationException.class})
	public LocalityRatings createLocalityRating(Integer userId,
			Integer localityId, LocalityRatings localityReview) {
		logger.debug("create locality rating for user {} locality {}",userId, localityId);
		LocalityRatings created = null;
		localityReview.setLocalityId(localityId);
		/*
		 * if non logged in user or unregistered user is trying to create rating
		 * then making user id 0
		 */
		if (userId == null) {
			localityReview.setUserId(0);
			localityReview.setReviewId(null);
			created = localityRatingDao.save(localityReview);
		}
		else{
			//find if rating already exist for this user and locality then update that
			LocalityRatings ratingPresent = localityRatingDao.findByUserIdAndLocalityId(userId, localityId);
			if(ratingPresent != null){
				//update already existing ratings
				created = updateLocalityRating(ratingPresent, localityReview);
			}
			else{
				//creating new rating by user for locality
				localityReview.setUserId(userId);
				created = localityRatingDao.save(localityReview);
			}
		}
		
		return created;
	}

	/**
	 * Update existing rating
	 * @param ratingPresent
	 * @param newRatings
	 */
	@Transactional
	private LocalityRatings updateLocalityRating(LocalityRatings ratingPresent,
			LocalityRatings newRatings) {
		ratingPresent.update(newRatings);
		return ratingPresent;
	}
	
	/**
	 * Get locality rating details for particular locality of user
	 * @param userId
	 * @param localityId
	 * @return
	 */
	public LocalityRatings getLocalityRatingOfUser(Integer userId, Integer localityId){
		LocalityRatings localityRating = localityRatingDao.findByUserIdAndLocalityId(userId, localityId);
		return localityRating;
	}
}
