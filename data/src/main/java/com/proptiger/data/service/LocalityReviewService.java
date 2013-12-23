package com.proptiger.data.service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

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
	
	@Resource
	private LocalityReviewDao localityReviewDao;
	@Resource
	private LocalityRatingDao localityRatingDao;
	
	private static Logger logger = LoggerFactory.getLogger(LocalityReviewService.class);
	/**
	 * Finds all review for a locality based on locality id
	 * 
	 * @param localityId
	 * @return
	 */
	public Map<String, Object> findReviewByLocalityId(int localityId,
			Pageable pageable) {
		logger.debug("findReviewByLocalityId, id=" + localityId);

		Long totalReviews = getLocalityReviewCount(localityId);

		if (pageable == null && totalReviews != null
				&& totalReviews.longValue() > 0)
			pageable = new PageRequest(0, totalReviews.intValue());
		else if (pageable == null)
			pageable = new PageRequest(0, 5);

		List<Object> reviewComments = localityReviewDao
				.getReviewCommentsByLocalityId(localityId, pageable);
		Object[] total = getLocalityRating(localityId);

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
		response.put(AVERAGE_RATINGS, total[0]);
		response.put(TOTAL_RATINGS, total[1]);
		response.put(REVIEWS, reviewComments);

		return response;
	}
	
	/**
	 * Get locality review count
	 * @param localityId
	 * @return
	 */
	public Long getLocalityReviewCount(int localityId){
		return localityReviewDao.getTotalReviewsByLocalityId(localityId);
	}
	
	public Object[] getLocalityRating(int localityId){
		return localityRatingDao
		.getAvgAndTotalRatingByLocalityId(localityId);
	}
}
