package com.proptiger.data.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.proptiger.data.model.LocalityAmenityTypes;

/**
 * @author Rajeev Pandey
 *
 */
public interface LocalityAmenityTypeDao extends JpaRepository<LocalityAmenityTypes, Integer>{

}
