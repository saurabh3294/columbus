package com.proptiger.data.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.proptiger.data.model.Amenity;

public interface ProjectAmenityDao extends JpaRepository<Amenity, Long>{

	public List<Amenity> findAmenitiesByProjectId(long projectId);
}
