package com.proptiger.data.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proptiger.data.model.Amenity;
import com.proptiger.data.repo.ProjectAmenityDao;

@Service
public class ProjectAmenityService {

	@Autowired
	private ProjectAmenityDao projectAmenityDao;
	
	public List<Amenity> getAmenitiesByProjectId(long projectId){
		return projectAmenityDao.findAmenitiesByProjectId(projectId);
	}
}
