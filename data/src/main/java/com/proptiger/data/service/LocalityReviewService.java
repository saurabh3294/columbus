package com.proptiger.data.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.proptiger.data.model.Locality;
import com.proptiger.data.model.ReviewComments;
import com.proptiger.data.pojo.LimitOffsetPageRequest;
import com.proptiger.data.pojo.Selector;
import com.proptiger.data.repo.LocalityRatingDao;
import com.proptiger.data.repo.LocalityReviewDao;


/**
 * Service class to handle CRUD operations for locality review details.
 * 
 * @author Rajeev Pandey
 *
 */
@Service
public class LocalityReviewService {

	public static final String COMMENT_TIME = "commentTime";
	public static final String USERNAME = "username";
	public static final String REVIEW_LABEL = "reviewLabel";
	public static final String REVIEW = "review";
	public static final String REVIEWS = "reviews";
	public static final String TOTAL_RATINGS = "totalRatings";
	public static final String AVERAGE_RATINGS = "averageRatings";
	public static final String TOTAL_REVIEWS = "totalReviews";
	public static final String TOTAL_USERS_BY_RATING = "totalUsersByRating";
	
	@Resource
	private LocalityReviewDao localityReviewDao;
	@Resource
	private LocalityRatingDao localityRatingDao;
	
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
	public Map<String, Object> findReviewByLocalityId(int localityId,
			Pageable pageable) {
		logger.debug("findReviewByLocalityId, id=" + localityId);

		Long totalReviews = getLocalityReviewCount(localityId);

		if (pageable == null && totalReviews != null
				&& totalReviews.longValue() > 0)
			pageable = new LimitOffsetPageRequest(0, totalReviews.intValue());
		else if (pageable == null)
			pageable = new LimitOffsetPageRequest(0, 5);

		List<Object> reviewComments = localityReviewDao
				.getReviewCommentsByLocalityId(localityId, pageable);
		
		Map<String, Object> ratingDetails = getTotalUsersByRatingByLocalityId(localityId);

		Map<String, Object> reviewCommentsMaps;
		Object[] reviewCommentsRow;
		int totalElements = reviewComments.size();
		for (int i = 0; i < totalElements; i++) {
			reviewCommentsRow = (Object[]) reviewComments.get(i);

			reviewCommentsMaps = new LinkedHashMap<String, Object>();
			reviewCommentsMaps.put(REVIEW, reviewCommentsRow[0]);
			reviewCommentsMaps.put(REVIEW_LABEL, reviewCommentsRow[1]);
			reviewCommentsMaps.put(USERNAME, reviewCommentsRow[2] == null ? reviewCommentsRow[4] : reviewCommentsRow[2]);
			reviewCommentsMaps.put(COMMENT_TIME, reviewCommentsRow[3]);

			reviewComments.set(i, reviewCommentsMaps);
		}

		Map<String, Object> response = new LinkedHashMap<String, Object>();

		response.put(TOTAL_REVIEWS, totalReviews);
		response.put(REVIEWS, reviewComments);
		if(ratingDetails != null)
		{
			response.putAll(ratingDetails);
		}

		return response;
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
	 * This method will return the distribution of rating by their total users, average rating.
	 * @param localityId
	 * @return Map<String, Object> The information contained is as:
	 *         1: TOTAL_USERS_BY_RATING => Map<Double, Long> Here Double will be rating and Long 
	 *                                     will be number of users.
	 *         2: AVERAGE_RATING => The average rating of locality.
	 *         3: TOTAL_RATINGS => total rating users.        
	 */
	public Map<String, Object> getTotalUsersByRatingByLocalityId(int localityId){
		List<Object[]> ratingDetails = localityRatingDao.getTotalUsersByRating(localityId);
		if(ratingDetails == null || ratingDetails.size() < 1)
			return  null;
		
		Map<Double, Long> ratingMap = new LinkedHashMap<>();
				
		Object[] ratingInfo;
		double avgRating = 0, rating = 0;
		long totalRating = 0, users = 0;
		
		Float ratingValue;
				
		for(int i=0; i<ratingDetails.size(); i++){
			ratingInfo = ratingDetails.get(i);
			rating = (ratingValue = (Float)ratingInfo[0]).doubleValue();
			users = (Long)ratingInfo[1];
			
			avgRating += rating*users;
			totalRating += users;
			ratingMap.put( rating, users);
		}
		if(totalRating < 1)
			totalRating = 1;
		avgRating /= totalRating;
				
		Map<String, Object> ratingResponse = new HashMap<>();
		ratingResponse.put(TOTAL_USERS_BY_RATING, ratingMap);
		ratingResponse.put(AVERAGE_RATINGS, avgRating);
		ratingResponse.put(TOTAL_RATINGS, totalRating);
		
		return ratingResponse;
	}
	
	public List<Integer> getTopReviewedLocalityOnCityOrSuburb(int locationType, int locationId, int minCount, Pageable pageable){
		return localityReviewDao.getTopReviewLocalitiesOnSuburbOrCity(locationType, locationId, minCount, pageable);
	}
		
	public List<Integer> getTopReviewedNearLocalitiesForLocality(int localityId, int minCount, Pageable pageable){
		int distance[] = {0, 5, 10, 15};
		int limit = pageable.getPageSize();
		Locality locality = localityService.getLocality(localityId);
		
		List<Integer> localityIds = new ArrayList<>();
		for(int i=0; i<distance.length-1 && localityIds.size()<limit; i++){
			List<Integer> localities = localityService.getNearLocalityIdOnLocalityOnConcentricCircle(locality, distance[i], distance[i+1]);  
			localityIds.addAll( localityReviewDao.getTopReviewNearLocalitiesOnLocality(localities, minCount, pageable) );
			pageable = new LimitOffsetPageRequest(0, limit - localityIds.size());
		}
		if(localityIds.size() < limit)
			localityIds.addAll(getTopReviewedLocalityOnCityOrSuburb(1, locality.getSuburb().getCityId(), minCount, pageable));
		
		// sending the unique localityIds.
		return new ArrayList<Integer>(new HashSet<Integer>(localityIds) );
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
	public List<ReviewComments> getLocalityReview(Integer localityId,
			Integer userId, Selector selector) {
		
		Pageable pageable = new LimitOffsetPageRequest();
		if (selector != null && selector.getPaging() != null) {
			pageable = new LimitOffsetPageRequest(selector.getPaging()
					.getStart(), selector.getPaging().getRows());
		}
		List<ReviewComments> reviews = null;

		//in case it call is for specific user
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
	public ReviewComments createReviewComment(Integer localityId,
			ReviewComments reviewComment, Integer userId) {
		validateReviewComment(reviewComment);
		ReviewComments reviewPresent = localityReviewDao.getByLocalityIdAndUserId(localityId, userId);
		if(reviewPresent != null){
			//TODO if review already present then probably update this
			
			return reviewPresent;
		}
		else{
			//create new review
			reviewComment.setUserId(userId);
			ReviewComments createComment = localityReviewDao.save(reviewComment);
			return createComment;
		}
	}

	/**
	 * Validate fields of ReviewComment.
	 * @param reviewComment
	 */
	private void validateReviewComment(ReviewComments reviewComment) {
		
	}
}
