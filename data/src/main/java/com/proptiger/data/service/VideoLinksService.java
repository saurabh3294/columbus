package com.proptiger.data.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proptiger.data.model.VideoLinks;
import com.proptiger.data.model.enums.DomainObject;
import com.proptiger.data.repo.VideoLinksDao;
import com.proptiger.data.util.IdConverterForDatabase;

@Service
public class VideoLinksService {
	@Autowired
	private VideoLinksDao videoLinksDao;
	
	public List<VideoLinks> getProjectVideoLinks(int projectId){
		int cmsProjectId = IdConverterForDatabase.getCMSDomainIdForDomainTypes(DomainObject.project.getText(), projectId);
		System.out.println(" CMS PROJECT ID" + cmsProjectId);
		List<VideoLinks> links = videoLinksDao.findByTableIdAndTableName(cmsProjectId, "resi_project");
		
		return links;
	}
}
