package com.proptiger.data.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proptiger.data.model.ProjectReview;
import com.proptiger.data.repo.ProjectReviewDao;

/**
 * Service class to handle CRUD operations for project review details.
 * 
 * @author Rajeev Pandey
 *
 */
@Service
public class ProjectReviewService {
	@Autowired
	private ProjectReviewDao projectReviewDao;
	
	/**
	 * Get project review based on project id
	 * @param projectId
	 * @return
	 */
	public List<ProjectReview> getProjectReviewByProjectId(Long projectId) {
		return projectReviewDao.findReviewsByProjectId(projectId);
		
	}
}
