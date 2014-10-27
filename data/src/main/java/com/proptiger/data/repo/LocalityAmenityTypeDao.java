package com.proptiger.data.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.proptiger.core.model.cms.LandMarkTypes;

/**
 * @author Rajeev Pandey
 * 
 */
public interface LocalityAmenityTypeDao extends JpaRepository<LandMarkTypes, Integer> {

}
