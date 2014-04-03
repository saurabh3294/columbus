/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proptiger.data.repo;

import java.io.Serializable;
import java.util.List;

import org.jboss.logging.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.proptiger.data.model.LocalityAmenity;

/**
 * 
 * @author mukand
 */
public interface LocalityAmenityDao extends JpaRepository<LocalityAmenity, Serializable> {

    @Query("SELECT LA FROM LocalityAmenity LA JOIN FETCH LA.localityAmenityTypes as LAT WHERE " + " LA.placeTypeId = LAT.id AND LA.localityId = ?1 ")
    public List<LocalityAmenity> getAmenitiesByLocalityId(@Param Integer localityId);

    @Query("SELECT LA FROM LocalityAmenity LA JOIN FETCH LA.localityAmenityTypes as LAT WHERE " + " LA.placeTypeId = LAT.id AND LA.localityId = ?1 AND LAT.name = ?2 ")
    public List<LocalityAmenity> getAmenitiesByLocalityIdAndAmenity(@Param Integer localityId, @Param String amenityName);

    @Query("SELECT LA FROM LocalityAmenity LA JOIN FETCH LA.localityAmenityTypes as LAT WHERE " + " LA.placeTypeId = LAT.id AND LA.cityId = ?1 ")
    public List<LocalityAmenity> getAmenitiesByCityId(@Param Integer cityId);

    @Query("SELECT LA FROM LocalityAmenity LA JOIN FETCH LA.localityAmenityTypes as LAT WHERE " + " LA.placeTypeId = LAT.id AND LA.cityId = ?1 AND LAT.name = ?2 ")
    public List<LocalityAmenity> getAmenitiesByCityIdAndAmenityName(@Param Integer cityId, @Param String amenityName);

    @Query("SELECT LA FROM LocalityAmenity LA JOIN FETCH LA.locality as L JOIN FETCH LA.localityAmenityTypes as LAT WHERE " + " L.suburbId = ?1 ")
    public List<LocalityAmenity> getAmenitiesBySuburbId(int suburbId);
    
}
