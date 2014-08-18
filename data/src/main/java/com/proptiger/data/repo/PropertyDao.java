package com.proptiger.data.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.proptiger.data.model.Property;

/**
 * @author Rajeev Pandey
 *
 */
@Repository
public interface PropertyDao extends JpaRepository<Property, Integer>, PropertyCustomDao{

}
