/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proptiger.data.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proptiger.data.model.Builder;
import com.proptiger.data.repo.BuilderDao;

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
