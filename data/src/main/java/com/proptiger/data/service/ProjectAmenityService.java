package com.proptiger.data.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proptiger.data.model.ProjectAmenity;
import com.proptiger.data.repo.ProjectAmenityDao;

/**
 * @author Rajeev Pandey
 *
 */
@Service
public class ProjectAmenityService {

	@Autowired
	private ProjectAmenityDao projectAmenityDao;
	
	public List<String> getAmenitiesByProjectId(long projectId){
		List<ProjectAmenity> list = projectAmenityDao.findAmenitiesByProjectId(projectId);
		List<String> amenityNameList = new ArrayList<String>();
		for(ProjectAmenity amenity: list){
			amenityNameList.add(amenity.getName());
		}
		return amenityNameList;
	}
}
