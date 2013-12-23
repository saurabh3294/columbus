/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proptiger.data.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proptiger.data.model.Project;
import com.proptiger.data.model.ProjectDB;
import com.proptiger.data.model.ProjectDiscussion;
import com.proptiger.data.model.ProjectSpecification;
import com.proptiger.data.pojo.Selector;
import com.proptiger.data.repo.ProjectDao;
import com.proptiger.data.repo.ProjectSpecificationDao;
import com.proptiger.data.service.pojo.SolrServiceResponse;
import com.proptiger.data.util.ResourceType;
import com.proptiger.data.util.ResourceTypeAction;
import com.proptiger.exception.ResourceNotAvailableException;

/**
 * 
 * @author mukand
 */
@Service
public class ProjectService {
    @Autowired
    private ProjectDao projectDao;

    @Autowired
    private ProjectSpecificationDao projectSpecificationDao;

    public SolrServiceResponse<List<Project>> getProjects(Selector projectFilter) {
        return projectDao.getProjects(projectFilter);
    }

    /**
     * Returns projects ordered by launch date (descending)
     *
     * @param cityName
     * @param projectFilter
     * @return
     */
    public SolrServiceResponse<List<Project>> getNewProjectsByLaunchDate(String cityName, Selector projectFilter) {
        return projectDao.getNewProjectsByLaunchDate(cityName, projectFilter);
    }

    /**
     * Returns projects with status 'Pre Launch' and 'Not Launched'.
     *
     * @param cityName
     * @param projectFilter
     * @return
     */
    public SolrServiceResponse<List<Project>> getUpcomingNewProjects(String cityName, Selector projectFilter) {
        return projectDao.getUpcomingNewProjects(cityName, projectFilter);
    }

    /**
     * Returns specifications of a project
     *
     * @param projectId
     * @return
     */
    public ProjectSpecification getProjectSpecifications(int projectId) {
        return projectSpecificationDao.findById(projectId);
    }

    /**
     * Returns all details of a project
     * @param projectId
     * @return
     */
    public ProjectDB getProjectDetails(Integer projectId) {
        ProjectDB project = projectDao.findByProjectId(projectId);
        if (project == null) {
            throw new ResourceNotAvailableException(ResourceType.PROJECT, ResourceTypeAction.GET);
        }
        return project;
    }

    /**
     * Returns all discussions for a project
     *
     * @param projectId
     * @param commentId
     * @return
     */
    public List<ProjectDiscussion> getDiscussions(int projectId, Integer commentId) {
        List<ProjectDiscussion> discussions = projectDao.getDiscussions(projectId, commentId);
        for (ProjectDiscussion projectDiscussion : discussions) {
            if ("proptiger".equals(projectDiscussion.getUser().getUsername())) {
                projectDiscussion.getUser().setUsername(projectDiscussion.getAdminUserName());
            }
        }

        return discussions;
    }
}
