package com.proptiger.data.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.proptiger.data.model.AmenityMaster;

/**
 * @author Rajeev Pandey
 *
 */
public interface AmenityMasterDao extends JpaRepository<AmenityMaster, Integer>{

    public List<AmenityMaster> findByAmenityIdIn(List<Integer> amenityIds);
}
