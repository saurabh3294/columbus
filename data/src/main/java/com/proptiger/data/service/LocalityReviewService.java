package com.proptiger.data.service;

import java.util.List;

import javax.annotation.Resource;

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
	
	/**
	 * Finds all review for a locality based on locality id
	 * 
	 * @param localityId
	 * @return
	 */
	public List<LocalityReview> findReviewByLocalityId(long localityId){
		return localityReviewDao.findReviewsByLocalityId(localityId);
	}
}
