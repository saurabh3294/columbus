package com.proptiger.data.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.proptiger.data.model.VideoLinks;
import com.proptiger.data.model.enums.DomainObject;
import com.proptiger.data.repo.VideoLinksDao;
import com.proptiger.data.util.Constants;
import com.proptiger.data.util.IdConverterForDatabase;

@Service
public class VideoLinksService {
    @Autowired
    private VideoLinksDao videoLinksDao;

    @Cacheable(value = Constants.CacheName.PROJECT_VIDEOS, key = "#projectId")
    public List<VideoLinks> getProjectVideoLinks(int projectId) {
        int cmsProjectId = IdConverterForDatabase.getCMSDomainIdForDomainTypes(DomainObject.project, projectId);
        return videoLinksDao.findByTableIdAndTableName(cmsProjectId, "resi_project");
    }
}
