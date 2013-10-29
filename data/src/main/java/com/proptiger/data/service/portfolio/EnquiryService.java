package com.proptiger.data.service.portfolio;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proptiger.data.repo.EnquiryDao;
import com.proptiger.data.repo.ForumUserDao;

/**
 * @author Rajeev Pandey
 *
 */
@Service
public class EnquiryService {

	@Autowired
	private EnquiryDao enquiryDao;
	
	@Autowired
	private ForumUserDao forumUserDao;
	
	public List<Map<String, String>> getEnquiries(Integer userId){
		String email = forumUserDao.findEmailByUserId(userId);
		List<Object[]> list = enquiryDao.findEnquiriesByEmail(email);
		List<Map<String, String>> result = convertToMap(list);
		return result;
	}

	private List<Map<String, String>> convertToMap(List<Object[]> list) {
		List<Map<String, String>> result = new ArrayList<Map<String, String>>();
		if(result != null){
			for(Object[] rowValues: list){
				Map<String, String> map = new HashMap<>();
				map.put("projectName", (String)rowValues[0]);
				map.put("cityName", (String)rowValues[1]);
				map.put("projectUrl", (String)rowValues[2]);
				result.add(map);
			}
		}
		return result;
	}
}
