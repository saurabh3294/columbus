/**
 * 
 */
package com.proptiger.data.service;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.proptiger.data.model.City;
import com.proptiger.data.model.URLDetail;
import com.proptiger.data.util.PageType;

/**
 * 
 *
 */
@Service
public class URLService {
	@Autowired
	private CityService cityService;
	
	
/* This function(parse) parses the url to determine the pageType and sets the required fields in urlDetail */
	
	public URLDetail parse(String URL) throws IllegalAccessException,
			InvocationTargetException {
		URLDetail urlDetail = new URLDetail();
		List<String> groups = new ArrayList<String>();
		
		for (PageType pageType : PageType.values()) {
			Pattern pattern = Pattern.compile(pageType.getRegex());
			Matcher matcher = pattern.matcher(URL);
			if (matcher.matches()) {
				int c = matcher.groupCount();
				for (int j = 0; j < c; j++) {
					groups.add(matcher.group(j + 1));
				}

				urlDetail.setPageType(pageType);
				int i = 0;

				for (String field : pageType.getURLDetailFields()) {
					BeanUtils.copyProperty(urlDetail, field, groups.get(i++));
				}
				break;
			}
		}
		
		urlDetail.setUrl(URL);
		return urlDetail;
	}
}
