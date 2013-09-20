/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proptiger.data.service;

import com.proptiger.data.model.Builder;
import com.proptiger.data.repo.BuilderDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;

/**
 *
 * @author mukand
 */
@Service
public class BuilderService {
    @Autowired
    private BuilderDao builderDao;
    
    public Builder getBuilderDetailsByProjectId(int projectId){
        return builderDao.findByProjectId(projectId);
    }
}
