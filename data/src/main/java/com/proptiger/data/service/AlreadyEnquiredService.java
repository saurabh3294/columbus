package com.proptiger.data.service;

import java.util.Calendar;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
	
	@Value("${enquired.within.days}")
	private Integer enquiredWithinDays;
	
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
	public AlreadyEnquiredDetails hasEnquired(Integer  projectId, Integer userId){
		String email = forumUserDao.findEmailByUserId(userId);
		Enquiry enquiry = null;
		AlreadyEnquiredDetails alreadyEnquiredDetails = new AlreadyEnquiredDetails(null, false, enquiredWithinDays);
		if(projectId != null){
			enquiry = enquiryDao.findEnquiryByEmailAndProjectId(email, new Long(projectId));
			if(enquiry != null){
				alreadyEnquiredDetails.setLastEnquiredOn(enquiry.getCreatedDate());
				Calendar cal = Calendar.getInstance();
				cal.add(Calendar.DATE, - enquiredWithinDays);
				Date date = cal.getTime();
				if(enquiry.getCreatedDate().compareTo(date) >= 0){
					alreadyEnquiredDetails.setEnquiredWithinTimeLimit(true);
				}
			}
		}
		return alreadyEnquiredDetails;
	}
	public static class AlreadyEnquiredDetails{
		//last enquiry date
		private Date lastEnquiredOn;
		//true if user enquired with last {enquiredWithinDays} number of days
		private boolean enquiredWithinTimeLimit = false;
		//number of days within which enquiry is done
		private int enquiredWithinDays;
		public AlreadyEnquiredDetails(Date lastEnquiredOn,
				boolean enquiredWithinTimeLimit, int enquiredWithinDays) {
			super();
			this.lastEnquiredOn = lastEnquiredOn;
			this.enquiredWithinTimeLimit = enquiredWithinTimeLimit;
			this.enquiredWithinDays = enquiredWithinDays;
		}
		public Date getLastEnquiredOn() {
			return lastEnquiredOn;
		}
		public boolean isEnquiredWithinTimeLimit() {
			return enquiredWithinTimeLimit;
		}
		public int getEnquiredWithinDays() {
			return enquiredWithinDays;
		}
		public void setLastEnquiredOn(Date lastEnquiredOn) {
			this.lastEnquiredOn = lastEnquiredOn;
		}
		public void setEnquiredWithinTimeLimit(boolean enquiredWithinTimeLimit) {
			this.enquiredWithinTimeLimit = enquiredWithinTimeLimit;
		}
		public void setEnquiredWithinDays(int enquiredWithinDays) {
			this.enquiredWithinDays = enquiredWithinDays;
		}
		
	}
}
