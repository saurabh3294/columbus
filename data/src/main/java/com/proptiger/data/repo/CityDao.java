package com.proptiger.data.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.proptiger.core.model.cms.City;

/**
 * @author Rajeev Pandey
 * @author Tokala Sai Teja
 * 
 */
@Repository
public interface CityDao extends JpaRepository<City, Integer>, CityCustomDao {

}
