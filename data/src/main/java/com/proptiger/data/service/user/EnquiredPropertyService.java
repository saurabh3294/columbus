package com.proptiger.data.service.user;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proptiger.core.model.proptiger.Enquiry.EnquiryCustomDetails;
import com.proptiger.data.repo.EnquiryDao;
import com.proptiger.data.repo.user.UserDao;

/**
 * Enquiry service class to provide enquiry details done by user
 * 
 * @author Rajeev Pandey
 * 
 */
@Service
public class EnquiredPropertyService {

    @Autowired
    private EnquiryDao   enquiryDao;

    @Autowired
    private UserDao      userDao;

    /**
     * Get enquiries for user
     * 
     * @param userId
     * @return
     */
    public List<EnquiryCustomDetails> getEnquiries(Integer userId) {
        List<EnquiryCustomDetails> list = enquiryDao.findEnquiriesByEmail(userDao.findById(userId).getEmail());
        return list;
    }
}
