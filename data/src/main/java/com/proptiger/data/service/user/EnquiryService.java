package com.proptiger.data.service.user;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proptiger.data.model.Enquiry.EnquiryCustomDetails;
import com.proptiger.data.repo.EnquiryDao;
import com.proptiger.data.repo.ForumUserDao;

/**
 * Enquiry service class to provide enquiry details done by user
 * 
 * @author Rajeev Pandey
 * 
 */
@Service
public class EnquiryService {

    @Autowired
    private EnquiryDao   enquiryDao;

    @Autowired
    private ForumUserDao forumUserDao;

    /**
     * Get enquiries for user
     * 
     * @param userId
     * @return
     */
    public List<EnquiryCustomDetails> getEnquiries(Integer userId) {
        String email = forumUserDao.findEmailByUserId(userId);
        List<EnquiryCustomDetails> list = enquiryDao.findEnquiriesByEmail(email);
        return list;
    }
}
