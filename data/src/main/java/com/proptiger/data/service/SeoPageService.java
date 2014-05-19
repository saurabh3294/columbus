package com.proptiger.data.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proptiger.data.model.City;
import com.proptiger.data.model.SeoPage;

@Service
public class SeoPageService {

	@Autowired
	private ProjectService projectService;
	
	@Autowired
	private CityService cityService;
	
	public SeoPage getCityOverviewPageSeo(int cityId){
		City city = cityService.getCity(cityId);
		
		return new SeoPage();
	}
	
}
