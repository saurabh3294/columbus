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

import com.proptiger.core.model.cms.LandMark;

/**
 * 
 * @author mukand
 */
public interface LandMarkDao extends JpaRepository<LandMark, Serializable>, LandMarkCustomDao {

    @Deprecated
    @Query("SELECT LA FROM LandMark LA JOIN FETCH LA.localityAmenityTypes as LAT WHERE " + " LA.placeTypeId = LAT.id AND LA.localityId = ?1 ")
    public List<LandMark> getAmenitiesByLocalityId(@Param Integer localityId);

    @Deprecated
    @Query("SELECT LA FROM LandMark LA JOIN FETCH LA.localityAmenityTypes as LAT WHERE " + " LA.placeTypeId = LAT.id AND LA.localityId = ?1 AND LAT.name = ?2 ")
    public List<LandMark> getAmenitiesByLocalityIdAndAmenity(@Param Integer localityId, @Param String amenityName);

    @Deprecated
    @Query("SELECT LA FROM LandMark LA JOIN FETCH LA.localityAmenityTypes as LAT WHERE " + " LA.placeTypeId = LAT.id AND LA.cityId = ?1 ")
    public List<LandMark> getAmenitiesByCityId(@Param Integer cityId);

    @Deprecated
    @Query("SELECT LA FROM LandMark LA JOIN FETCH LA.localityAmenityTypes as LAT WHERE " + " LA.placeTypeId = LAT.id AND LA.cityId = ?1 AND LAT.name = ?2 ")
    public List<LandMark> getAmenitiesByCityIdAndAmenityName(@Param Integer cityId, @Param String amenityName);

    @Deprecated
    @Query("SELECT LA FROM LandMark LA JOIN FETCH LA.locality as L JOIN FETCH LA.localityAmenityTypes as LAT WHERE " + " L.suburbId = ?1 ")
    public List<LandMark> getAmenitiesBySuburbId(int suburbId);

}
