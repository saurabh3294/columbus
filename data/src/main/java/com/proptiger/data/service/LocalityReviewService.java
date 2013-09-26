package com.proptiger.data.service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

	@Resource
	private LocalityReviewDao localityReviewDao;
        @Resource
        private LocalityRatingDao localityRatingDao;
	
	private static Logger logger = LoggerFactory.getLogger("locality.review");
	/**
	 * Finds all review for a locality based on locality id
	 * 
	 * @param localityId
	 * @return
	 */
	public Map<String, Object> findReviewByLocalityId(int localityId, Pageable pageable){
		if(logger.isDebugEnabled()){
			logger.debug("findReviewByLocalityId, id="+localityId);
		}
                                
                //Pageable pageable = new PageRequest(0, 5);
                                        
                List<Object> reviewComments = localityReviewDao.getReviewCommentsByLocalityId(localityId, pageable);
                Long totalReviews = localityReviewDao.getTotalReviewsByLocalityId(localityId);
                Object[] total = localityRatingDao.getAvgAndTotalRatingByLocalityId(localityId);
                
                Map<String, Object> reviewCommentsMaps;
                Object[] reviewCommentsRow;
                int totalElements = reviewComments.size();
                for(int i=0; i<totalElements; i++)
                {
                    reviewCommentsRow = (Object[])reviewComments.get(i);
                    
                    reviewCommentsMaps = new LinkedHashMap<String, Object>();
                    reviewCommentsMaps.put("review", reviewCommentsRow[0]);
                    reviewCommentsMaps.put("reviewLabel", reviewCommentsRow[1]);
                    reviewCommentsMaps.put("username", reviewCommentsRow[2]);
                    reviewCommentsMaps.put("commentTime", reviewCommentsRow[3]);
                    
                    reviewComments.set(i, reviewCommentsMaps);
                }
                
                Map<String, Object> response = new LinkedHashMap<String, Object>();
                
                response.put("totalReviews", totalReviews);
                response.put("averageRatings", total[0]);
                response.put("totalRatings", total[1]);
                response.put("reviews", reviewComments);
                 
                return response;
        }
}
