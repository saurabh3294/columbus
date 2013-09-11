package com.proptiger.data.service;

import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.proptiger.data.model.LocalityReview;
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
	
	private static Logger logger = LoggerFactory.getLogger("locality.review");
	/**
	 * Finds all review for a locality based on locality id
	 * 
	 * @param localityId
	 * @return
	 */
	public List<LocalityReview> findReviewByLocalityId(long localityId){
		if(logger.isDebugEnabled()){
			logger.debug("findReviewByLocalityId, id="+localityId);
		}
		return localityReviewDao.findReviewsByLocalityId(localityId);
	}
}
