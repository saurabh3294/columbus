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
	
	@Autowired
	private LocalityService localityService;
	
	/**
	 * Get list of city details
	 * @param selector
	 * @return List<City>
	 */
	public List<City> getCityList(Selector selector){
		return cityDao.getCities(selector);
	}
	
	/**
	 * This method will return the city object based on city id.
	 * @param cityId
	 * @return City.
	 */
	public City getCityInfo(int cityId){
		City city = cityDao.getCity(cityId);
		if(city==null)
			return null;
		
		city.setAvgBHKPrice( localityService.getAvgPricePerUnitAreaBHKWise( "cityId", cityId, city.getDominantUnitType() ) );
		return city;
	}
}
