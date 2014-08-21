package com.proptiger.data.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.proptiger.data.enums.DomainObject;
import com.proptiger.data.model.ProjectAmenity;
import com.proptiger.data.model.ProjectCMSAmenity;
import com.proptiger.data.repo.ProjectAmenityDao;
import com.proptiger.data.repo.ProjectCMSAmenityDao;
import com.proptiger.data.util.Constants;
import com.proptiger.data.util.IdConverterForDatabase;

/**
 * @author Rajeev Pandey
 * 
 */
@Service
public class ProjectAmenityService {

    @Autowired
    private ProjectAmenityDao    projectAmenityDao;

    @Autowired
    private ProjectCMSAmenityDao projectCMSAmenityDao;

    public List<String> getAmenitiesNameByProjectId(long projectId) {
        List<ProjectAmenity> list = getAmenitiesByProjectId(projectId);
        List<String> amenityNameList = new ArrayList<String>();
        for (ProjectAmenity amenity : list) {
            amenityNameList.add(amenity.getName());
        }
        return amenityNameList;
    }

    public List<ProjectAmenity> getAmenitiesByProjectId(long projectId) {
        return projectAmenityDao.findAmenitiesByProjectId(projectId);
    }

    @Cacheable(value = Constants.CacheName.PROJECT_CMS_AMENITY, key = "#projectId")
    public List<ProjectCMSAmenity> getCMSAmenitiesByProjectId(int projectId) {
        return projectCMSAmenityDao.findByProjectId(IdConverterForDatabase.getCMSDomainIdForDomainTypes(
                DomainObject.project,
                projectId));
    }
    
    public List<ProjectCMSAmenity> getCMSAmenitiesByProjectIdAndAmenityIds(int projectId, List<Integer> masterAmenitieIds) {
        if(masterAmenitieIds != null && masterAmenitieIds.size() > 0){
            Set<Integer> ids = new HashSet<>(masterAmenitieIds);
            return projectCMSAmenityDao.findByProjectIdAndMasterAmenityIds(projectId, ids);
        }
        return new ArrayList<>();
    }
}
