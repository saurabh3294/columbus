/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proptiger.data.repo;

import java.io.Serializable;

import org.jboss.logging.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.proptiger.data.model.LocalityAmenity;

/**
 *
 * @author mukand
 */
public interface LocalityAmenityDao extends JpaRepository<LocalityAmenity, Serializable>{
    
    @Query("SELECT LA.localityId, LA.cityId, LA.name, LA.reference, LA.address, LA.latitude, LA.longitude, LA.phoneNumber, LA.vicinity FROM LocalityAmenity LA JOIN LA.localityAmenityTypes as LAT WHERE "
            + " LA.placeTypeId = LAT.id AND LA.localityId = ?1 ")
    public Object[] getAmenitiesByLocalityId(@Param int localityId);
    
    @Query("SELECT LA.localityId, LA.cityId, LA.name, LA.reference, LA.address, LA.latitude, LA.longitude, LA.phoneNumber, LA.vicinity FROM LocalityAmenity LA JOIN LA.localityAmenityTypes as LAT WHERE "
            + " LA.placeTypeId = LAT.id AND LA.localityId = ?1 AND LAT.name = ?2 ")
    public Object[] getAmenitiesByLocalityIdAndAmenity(@Param int localityId, @Param String AmenityName);
}

/*
 * @Query("SELECT LA.localityId, LA.cityId, LA.name, LA.reference, LA.address, LA.latitude, LA.longitude, LA.phoneNumber, LA.vicinity FROM LocalityAmenity LA JOIN LA.localityAmenityTypes as LAT WHERE "
            + " LA.placeTypeId = LAT.id AND localityId = ?1 AND CASE ?3 WHEN 1 THEN '?2' WHEN 2 THEN LAT.name  END  = '?2' ")
 */