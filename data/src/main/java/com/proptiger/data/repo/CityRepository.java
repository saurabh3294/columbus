package com.proptiger.data.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.proptiger.core.model.cms.City;

/**
 * @author rajeev-engg-lp
 * 
 */
public interface CityRepository extends JpaRepository<City, Integer> {

}
