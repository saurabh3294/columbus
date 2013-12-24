package com.proptiger.data.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proptiger.data.model.City;
import com.proptiger.data.pojo.Selector;
import com.proptiger.data.repo.CityDao;

/**
 * Service class to get city data
 * 
 * @author Rajeev Pandey
 * 
 */
@Service
public class CityService {
	@Autowired
	private CityDao cityDao;
	
	/**
	 * Get list of city details
	 * @param selector
	 * @return
	 */
	public List<City> getCityList(Selector selector){
		return cityDao.getCities(selector);
	}
}
