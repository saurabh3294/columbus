/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proptiger.data.service;

import com.proptiger.data.model.Project;
import com.proptiger.data.model.filter.ProjectFilter;
import com.proptiger.data.repo.ProjectDao;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author mukand
 */
@Service
public class ProjectService {
    @Autowired
    private ProjectDao projectDao;
    
    public List<Project> getProjects(ProjectFilter projectFilter){
        return projectDao.getProjects(projectFilter);
    }
}
