package com.proptiger.data.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.proptiger.core.enums.DomainObject;
import com.proptiger.core.model.cms.VideoLinks;
import com.proptiger.core.util.Constants;
import com.proptiger.data.repo.VideoLinksDao;
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
