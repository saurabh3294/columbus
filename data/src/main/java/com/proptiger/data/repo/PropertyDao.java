package com.proptiger.data.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.proptiger.data.model.Property;

/**
 * @author Rajeev Pandey
 *
 */
@Repository
public interface PropertyDao extends JpaRepository<Property, Integer>, PropertyCustomDao{

 @Query(" SELECT P FROM Property P JOIN fetch P.project PR WHERE P.propertyId IN ?1 AND PR.version = 'Website' ")
    public List<Property> findByPropertyIdsList(List<Integer> propertyIds);
}