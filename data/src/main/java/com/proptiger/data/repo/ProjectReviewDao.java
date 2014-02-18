package com.proptiger.data.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.proptiger.data.model.ProjectReview;

/**
 * Dao class to handle CRUD operation for Project review
 * 
 * @author Rajeev Pandey
 * 
 */
@Deprecated
public interface ProjectReviewDao extends JpaRepository<ProjectReview, Long> {

    /**
     * Finds all review for a project based on project id
     * 
     * @param projectId
     * @return
     */
    public List<ProjectReview> findReviewsByProjectId(Long projectId);
}
