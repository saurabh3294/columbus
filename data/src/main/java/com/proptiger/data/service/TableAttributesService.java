package com.proptiger.data.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.proptiger.core.model.cms.TableAttributes;
import com.proptiger.data.repo.TableAttributesDao;
import com.proptiger.data.util.Constants;

@Service
public class TableAttributesService {
    @Autowired
    TableAttributesDao tableAttributesDao;

    @Cacheable(value = Constants.CacheName.PROJECT_SPECIFICATION)
    @CachePut(value = "cache", key = "#projectId+\":\"+#tableName")
    public List<TableAttributes> getTableAttributes(int projectId, String tableName) {
        return tableAttributesDao.findByTableIdAndTableName(projectId, "resi_project");
    }

}
