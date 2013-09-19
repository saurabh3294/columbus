package com.proptiger.data.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.proptiger.data.model.Project_Amenity;

/**
 * @author Rajeev Pandey
 *
 */
public interface ProjectAmenityDao extends JpaRepository<Project_Amenity, Long>{

	public List<Project_Amenity> findAmenitiesByProjectId(long projectId);
}
