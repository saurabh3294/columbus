package com.proptiger.data.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.proptiger.data.model.ProjectAmenity;

/**
 * @author Rajeev Pandey
 * 
 */
public interface ProjectAmenityDao extends JpaRepository<ProjectAmenity, Long> {

    public List<ProjectAmenity> findAmenitiesByProjectId(long projectId);
}
