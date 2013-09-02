package com.proptiger.data.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.proptiger.data.model.LocalityReview;
import com.proptiger.data.repo.LocalityReviewDao;


/**
 * @author Rajeev Pandey
 *
 */
@Service
public class LocalityReviewService {

	@Resource
	private LocalityReviewDao localityReviewDao;
	
	public List<LocalityReview> findReviewByLocalityId(long localityId){
		return localityReviewDao.findReviewsByLocalityId(localityId);
	}
}
