package com.proptiger.data.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
@Deprecated
public class ProjectReviewService {
	@Autowired
	private ProjectReviewDao projectReviewDao;
	
	private static Logger logger = LoggerFactory.getLogger("project.review");
	/**
	 * Get project review based on project id
	 * @param projectId
	 * @return
	 */
	public List<ProjectReview> getProjectReviewByProjectId(Long projectId) {
		if(logger.isDebugEnabled()){
			logger.debug("Get Project Review By ProjectId, id="+projectId);
		}
		return projectReviewDao.findReviewsByProjectId(projectId);
		
	}
}
