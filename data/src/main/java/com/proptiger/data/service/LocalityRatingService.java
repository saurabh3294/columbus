package com.proptiger.data.service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.proptiger.data.model.LocalityRatings;
import com.proptiger.data.model.LocalityRatings.LocalityAverageRatingCategory;
import com.proptiger.data.model.LocalityRatings.LocalityRatingDetails;
import com.proptiger.data.model.LocalityRatings.LocalityRatingUserCount;
import com.proptiger.data.repo.LocalityRatingDao;
import com.proptiger.exception.ConstraintViolationException;

/**
 * Service class to provide CRUD operations over locality ratings
 * @author Rajeev Pandey
 * 
 */
@Service
public class LocalityRatingService {

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
	public LocalityRatingDetails getUsersCountByRatingOfLocality(int localityId) {
		List<LocalityRatingUserCount> ratingWiseUserCountList = localityRatingDao
				.getTotalUsersByRating(localityId);
		if (ratingWiseUserCountList == null
				|| ratingWiseUserCountList.size() < 1) {
			return null;
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

		LocalityRatingDetails localityRatingDetails = new LocalityRatingDetails(
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
	public LocalityAverageRatingCategory getAvgRagingsOfLocalityCategory(Integer localityId){
		LocalityAverageRatingCategory avgRatingOfAmenities = localityRatingDao.getAvgRatingOfAmenitiesForLocality(localityId);
		return avgRatingOfAmenities;
	}
	
	/**
	 * This method either updates a existing ratings by user for locality or creates new ratings for locality.
	 * In case of non logged in or unregistered user, user id will be 0
	 * @param userId
	 * @param localityId
	 * @param localityReview
	 * @return
	 */
	@Transactional(rollbackFor = {ConstraintViolationException.class})
	public LocalityRatings createLocalityRating(Integer userId,
			Integer localityId, LocalityRatings localityReview) {
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
}
