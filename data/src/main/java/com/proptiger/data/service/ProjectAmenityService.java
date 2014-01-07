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
	
	public List<String> getAmenitiesNameByProjectId(long projectId){
		List<ProjectAmenity> list = getAmenitiesByProjectId(projectId);
		List<String> amenityNameList = new ArrayList<String>();
		for(ProjectAmenity amenity: list){
			amenityNameList.add(amenity.getName());
		}
		return amenityNameList;
	}
	
	public List<ProjectAmenity> getAmenitiesByProjectId(long projectId){
		return projectAmenityDao.findAmenitiesByProjectId(projectId);
	}
}
