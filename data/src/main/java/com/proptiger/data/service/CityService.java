package com.proptiger.data.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proptiger.data.model.City;
import com.proptiger.data.pojo.Selector;
import com.proptiger.data.repo.CityDao;

@Service
public class CityService {
	@Autowired
	private CityDao cityDao;
	
	public List<City> getCityList(Selector selector){
		return cityDao.getCities(selector);
	}
}
