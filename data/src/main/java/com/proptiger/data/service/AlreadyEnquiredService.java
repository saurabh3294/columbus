package com.proptiger.data.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proptiger.data.model.Enquiry;
import com.proptiger.data.repo.EnquiryDao;
import com.proptiger.data.repo.ForumUserDao;

/**
 * Service class to get if user have already enquired about an entity
 * @author Rajeev Pandey
 *
 */
@Service
public class AlreadyEnquiredService {

	@Autowired
	private EnquiryDao enquiryDao;
	
	@Autowired
	private ForumUserDao forumUserDao;
	
	/**
	 * Get if user have already enquired a entity
	 * @param projectId
	 * @param userId
	 * @return
	 */
	public boolean hasEnquired(Integer  projectId, Integer userId){
		String email = forumUserDao.findEmailByUserId(userId);
		Enquiry enquiry = null;
		if(projectId != null){
			enquiry = enquiryDao.findEnquiryByEmailAndProjectId(email, new Long(projectId));
			if(enquiry != null){
				return true;
			}
		}
		return false;
	}
}
