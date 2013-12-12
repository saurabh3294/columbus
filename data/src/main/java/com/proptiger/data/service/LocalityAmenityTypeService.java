package com.proptiger.data.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proptiger.data.model.LocalityAmenityTypes;
import com.proptiger.data.repo.LocalityAmenityTypeDao;

@Service
public class LocalityAmenityTypeService {

	@Autowired
	private LocalityAmenityTypeDao amenityTypeDao;
	
	private Map<Integer, LocalityAmenityTypes> amenitiesTypesMap;
	
	/**
	 * Get all types of available amenities
	 * @return
	 */
	public Map<Integer, LocalityAmenityTypes>  getLocalityAmenityTypes(){
		if(amenitiesTypesMap == null){
			amenitiesTypesMap = new HashMap<>();
			List<LocalityAmenityTypes> list = amenityTypeDao.findAll();
			if(list != null){
				for(LocalityAmenityTypes amenityType: list){
					amenitiesTypesMap.put(amenityType.getId(), amenityType);
				}
			}
		}
		return amenitiesTypesMap;
	}
}
