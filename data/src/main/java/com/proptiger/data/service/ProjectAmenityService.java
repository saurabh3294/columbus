package com.proptiger.data.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proptiger.data.model.ProjectAmenity;
import com.proptiger.data.model.ProjectCMSAmenity;
import com.proptiger.data.model.enums.DomainObject;
import com.proptiger.data.repo.ProjectAmenityDao;
import com.proptiger.data.repo.ProjectCMSAmenityDao;
import com.proptiger.data.util.IdConverterForDatabase;

/**
 * @author Rajeev Pandey
 *
 */
@Service
public class ProjectAmenityService {

	@Autowired
	private ProjectAmenityDao projectAmenityDao;
	
	@Autowired
	private ProjectCMSAmenityDao projectCMSAmenityDao;
	
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
	
	public List<ProjectCMSAmenity> getCMSAmenitiesByProjectId(int projectId) {
		return projectCMSAmenityDao.findByProjectId(IdConverterForDatabase
				.getCMSDomainIdForDomainTypes(DomainObject.project, projectId));
	}
}
