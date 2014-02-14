package com.proptiger.data.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.proptiger.data.model.Locality;
import com.proptiger.data.model.LocalityRatings.LocalityRatingDetails;
import com.proptiger.data.model.LocalityReviewComments;
import com.proptiger.data.model.LocalityReviewComments.LocalityReviewCustomDetail;
import com.proptiger.data.model.LocalityReviewComments.LocalityReviewRatingDetails;
import com.proptiger.data.pojo.LimitOffsetPageRequest;
import com.proptiger.data.pojo.Selector;
import com.proptiger.data.repo.LocalityReviewDao;
import com.proptiger.data.util.Constants;


/**
 * Service class to handle CRUD operations for locality review details.
 * 
 * @author Rajeev Pandey
 *
 */
@Service
public class LocalityReviewService {

	@Resource
	private LocalityReviewDao localityReviewDao;
	@Resource
	private LocalityRatingService localityRatingService;
	
	@Autowired
	private LocalityService localityService;
	
	private static Logger logger = LoggerFactory.getLogger(LocalityReviewService.class);
	/**
	 * Finds all review for a locality based on locality id
	 * 
	 * @param localityId
	 * @param Pageable number of reviews to return.
	 * @return Map<String, Object> the output is as follows:
	 *         TOTAL_REVIEWS : total reviews found on the locality.
	 *         REVIEWS: Reviews found filtered by pageable.
	 */
	@Cacheable(value = Constants.CacheName.LOCALITY_REVIEW_RATING, key = "#localityId +'-'+{#noOfReviews != null ?#noOfReviews:'' }")
	public LocalityReviewRatingDetails getLocalityReviewRatingDetails(int localityId,
			Integer noOfReviews) {
		logger.debug("Get review and rating details of locality {}", localityId);
		Long totalReviews = getLocalityReviewCount(localityId);
		LimitOffsetPageRequest pageable = new LimitOffsetPageRequest();
		
		if(noOfReviews != null && noOfReviews.intValue() > 0){
			pageable = new LimitOffsetPageRequest(0, noOfReviews);
		}
		else if(totalReviews != null && totalReviews.intValue() > 0){
			pageable = new LimitOffsetPageRequest(0, totalReviews.intValue());
		}
		
		List<LocalityReviewCustomDetail> reviewComments = getLocalityReviewCustomDetails(localityId, pageable);

		LocalityRatingDetails localityRatingDetails = localityRatingService
				.getUsersCountByRatingOfLocality(localityId);

		LocalityReviewRatingDetails localityReviewRatingDetails = new LocalityReviewRatingDetails(
				totalReviews, reviewComments, localityRatingDetails);
		return localityReviewRatingDetails;
	}

	/**
	 * Get few fields of locality review by locality id
	 * @param localityId
	 * @param pageable
	 * @return
	 */
	public List<LocalityReviewCustomDetail> getLocalityReviewCustomDetails(
			int localityId, Pageable pageable) {
		return localityReviewDao.getReviewCommentsByLocalityId(localityId,	pageable);
	}
	
	/**
	 * Get locality review count
	 * @param localityId
	 * @return Long Number of reviews
	 */
	public Long getLocalityReviewCount(int localityId){
		return localityReviewDao.getTotalReviewsByLocalityId(localityId);
	}
	
	/**
	 * Get top reviewed locality id for city or suburb
	 * @param locationType
	 * @param locationId
	 * @param minCount
	 * @param pageable
	 * @return
	 */
	public List<Integer> getTopReviewedLocalityOnCityOrSuburb(int locationType, int locationId, int minCount, Pageable pageable){
		return localityReviewDao.getTopReviewLocalitiesOnSuburbOrCity(locationType, locationId, minCount, pageable);
	}
		
	/**
	 * Get top reviewed locality ids near/around a given locality
	 * @param localityId
	 * @param minCount
	 * @param pageable
	 * @return
	 */
	public List<Integer> getTopReviewedNearLocalitiesForLocality(
			int localityId, int minCount, LimitOffsetPageRequest pageable) {
		int distance[] = { 0, 5, 10, 15 };
		int limit = pageable.getPageSize();
		Locality locality = localityService.getLocality(localityId);

		List<Integer> localityIds = new ArrayList<>();
		for (int i = 0; i < distance.length - 1 && localityIds.size() < limit; i++) {
			List<Integer> localities = localityService
					.getNearLocalityIdOnLocalityOnConcentricCircle(locality,
							distance[i], distance[i + 1]);
			localityIds.addAll(localityReviewDao
					.getTopReviewNearLocalitiesOnLocality(localities, minCount,
							pageable));
			pageable = new LimitOffsetPageRequest(0, limit - localityIds.size());
		}
		if (localityIds.size() < limit)
			localityIds.addAll(getTopReviewedLocalityOnCityOrSuburb(1, locality
					.getSuburb().getCityId(), minCount, pageable));

		// sending the unique localityIds.
		return new ArrayList<Integer>(new HashSet<Integer>(localityIds));
	}
	
	/**
	 * Get locality reviews for locality id. If user id is not null then reviews
	 * for that user id and locality id will be returned
	 * 
	 * @param localityId
	 * @param userId
	 * @param selector
	 * @return
	 */
	@Cacheable(value = Constants.CacheName.LOCALITY_REVIEW, key = "#localityId", condition = "#userId == null")
	public List<LocalityReviewComments> getLocalityReview(Integer localityId,
			Integer userId, Selector selector) {
		
		LimitOffsetPageRequest pageable = new LimitOffsetPageRequest();
		if (selector != null && selector.getPaging() != null) {
			pageable = new LimitOffsetPageRequest(selector.getPaging()
					.getStart(), selector.getPaging().getRows());
		}
		List<LocalityReviewComments> reviews = null;

		//in case call is for specific user
		if (userId != null) {
			reviews = localityReviewDao.getReviewsByLocalityIdAndUserId(
					localityId, userId, pageable);
		}
		else{
			reviews = localityReviewDao
					.getReviewsByLocalityId(localityId, pageable);
		}
		return reviews;
	}

	/**
	 * Create new locality review by user for locality
	 * @param localityId
	 * @param reviewComment
	 * @param userId
	 * @return
	 */
	@CacheEvict(value = {Constants.CacheName.LOCALITY_REVIEW, Constants.CacheName.LOCALITY_REVIEW_RATING}, key = "#localityId")
	public LocalityReviewComments createLocalityReviewComment(Integer localityId,
			LocalityReviewComments reviewComment, Integer userId) {
		validateReviewComment(reviewComment);
		LocalityReviewComments reviewPresent = localityReviewDao.getByLocalityIdAndUserId(localityId, userId);
		if(reviewPresent != null){
			//TODO if review already present then probably update this
			
			return reviewPresent;
		}
		else{
			//create new review
			reviewComment.setUserId(userId);
			LocalityReviewComments createComment = localityReviewDao.save(reviewComment);
			return createComment;
		}
	}

	/**
	 * Validate fields of ReviewComment.
	 * @param reviewComment
	 */
	private void validateReviewComment(LocalityReviewComments reviewComment) {
		
	}
}
