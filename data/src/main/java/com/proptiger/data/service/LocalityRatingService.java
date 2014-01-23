package com.proptiger.data.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.proptiger.data.model.LocalityReview;
import com.proptiger.data.model.LocalityReview.LocalityAverageRatingCategory;
import com.proptiger.data.repo.LocalityRatingDao;
import com.proptiger.exception.ConstraintViolationException;
import com.proptiger.exception.DuplicateNameResourceException;

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
	public LocalityReview createLocalityRating(Integer userId,
			Integer localityId, LocalityReview localityReview) {
		LocalityReview created = null;
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
			LocalityReview ratingPresent = localityRatingDao.findByUserIdAndLocalityId(userId, localityId);
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
	private LocalityReview updateLocalityRating(LocalityReview ratingPresent,
			LocalityReview newRatings) {
		ratingPresent.update(newRatings);
		return ratingPresent;
	}
}
