package com.proptiger.data.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proptiger.data.model.Amenity;
import com.proptiger.data.repo.ProjectAmenityDao;

@Service
public class ProjectAmenityService {

	@Autowired
	private ProjectAmenityDao projectAmenityDao;
	
	public List<String> getAmenitiesByProjectId(long projectId){
		List<Amenity> list = projectAmenityDao.findAmenitiesByProjectId(projectId);
		List<String> amenityNameList = new ArrayList<String>();
		for(Amenity amenity: list){
			amenityNameList.add(amenity.getAmenityName());
		}
		return amenityNameList;
	}
}
