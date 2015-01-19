package com.proptiger.data.service.user;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.proptiger.core.dto.internal.ActiveUser;
import com.proptiger.core.model.proptiger.Enquiry;
import com.proptiger.core.model.proptiger.Enquiry.EnquiryCustomDetails;
import com.proptiger.data.repo.EnquiryDao;

/**
 * Enquiry service class to provide enquiry details done by user
 * 
 * @author Rajeev Pandey
 * 
 */
@Service
public class UserEnquiredService {

    @Autowired
    private EnquiryDao   enquiryDao;

    @Value("${enquired.within.days}")
    private Integer                          enquiredWithinDays;

    /**
     * Get enquiries for user
     * 
     * @param userId
     * @return
     */
    public List<EnquiryCustomDetails> getEnquiries(ActiveUser activeUser) {
        List<EnquiryCustomDetails> list = enquiryDao.findEnquiriesByEmail(activeUser.getUsername());
        return list;
    }
    
    /**
     * Get if user have already enquired a entity
     * 
     * @param projectId
     * @param activeUser
     * @return
     */
    public AlreadyEnquiredDetails hasEnquired(Integer projectId, ActiveUser activeUser) {
        String email = activeUser.getUsername();
        Enquiry enquiry = null;
        AlreadyEnquiredDetails alreadyEnquiredDetails = new AlreadyEnquiredDetails(null, false, enquiredWithinDays);
        if (projectId != null) {
            List<Enquiry> enquiries = enquiryDao.findEnquiryByEmailAndProjectIdOrderByCreatedDateDesc(email, projectId);
            if (enquiries != null && !enquiries.isEmpty()) {
                enquiry = enquiries.get(0);
            }

            if (enquiry != null) {
                alreadyEnquiredDetails.setLastEnquiryDate(enquiry.getCreatedDate());
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.DATE, -enquiredWithinDays);
                Date date = cal.getTime();
                if (enquiry.getCreatedDate().compareTo(date) >= 0) {
                    alreadyEnquiredDetails.setHasValidEnquiry(true);
                }
            }
        }
        return alreadyEnquiredDetails;
    }
    
    public static class AlreadyEnquiredDetails {
        // last enquiry date
        private Date    lastEnquiryDate;
        // true if user enquired with last {enquiredWithinDays} number of days
        private boolean hasValidEnquiry = false;
        // number of days within which enquiry is done
        private int     enquiryValidityPeriod;

        public AlreadyEnquiredDetails(Date lastEnquiredOn, boolean enquiredWithinTimeLimit, int enquiredWithinDays) {
            super();
            this.lastEnquiryDate = lastEnquiredOn;
            this.hasValidEnquiry = enquiredWithinTimeLimit;
            this.enquiryValidityPeriod = enquiredWithinDays;
        }

        public Date getLastEnquiryDate() {
            return lastEnquiryDate;
        }

        public void setLastEnquiryDate(Date lastEnquiryDate) {
            this.lastEnquiryDate = lastEnquiryDate;
        }

        public boolean isHasValidEnquiry() {
            return hasValidEnquiry;
        }

        public void setHasValidEnquiry(boolean hasValidEnquiry) {
            this.hasValidEnquiry = hasValidEnquiry;
        }

        public int getEnquiryValidityPeriod() {
            return enquiryValidityPeriod;
        }

        public void setEnquiryValidityPeriod(int enquiryValidityPeriod) {
            this.enquiryValidityPeriod = enquiryValidityPeriod;
        }

    }


}
