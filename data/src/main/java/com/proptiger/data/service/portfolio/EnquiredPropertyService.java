package com.proptiger.data.service.portfolio;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proptiger.data.model.Enquiry;
import com.proptiger.data.repo.EnquiryDao;
import com.proptiger.data.repo.ForumUserDao;

/**
 * @author Rajeev Pandey
 *
 */
@Service
public class EnquiredPropertyService {

	@Autowired
	private EnquiryDao enquiryDao;
	
	@Autowired
	private ForumUserDao forumUserDao;
	
	public List<Enquiry> getEnquiries(Integer userId){
		return null;
	}
}
