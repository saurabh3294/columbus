package com.proptiger.data.service.portfolio;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proptiger.data.repo.EnquiryDao;
import com.proptiger.data.repo.ForumUserDao;

/**
 * Enquiry service class to provide enquiry details done by user
 * @author Rajeev Pandey
 *
 */
@Service
public class EnquiryService {

	@Autowired
	private EnquiryDao enquiryDao;
	
	@Autowired
	private ForumUserDao forumUserDao;
	
	/**
	 * Get enquiries for user
	 * @param userId
	 * @return
	 */
	public List<Map<String, Object>> getEnquiries(Integer userId){
		String email = forumUserDao.findEmailByUserId(userId);
		List<Object[]> list = enquiryDao.findEnquiriesByEmail(email);
		List<Map<String, Object>> result = convertToMap(list);
		return result;
	}

	private List<Map<String, Object>> convertToMap(List<Object[]> list) {
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		if(result != null){
			for(Object[] rowValues: list){
				int count = 0;
				Map<String, Object> map = new HashMap<>();
				map.put("projectName", (String)rowValues[count++]);
				map.put("cityName", (String)rowValues[count++]);
				map.put("projectUrl", (String)rowValues[count++]);
				map.put("createdDate", (Date)rowValues[count++]);
				result.add(map);
			}
		}
		return result;
	}
}
