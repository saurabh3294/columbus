/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proptiger.data.repo;

import com.proptiger.data.model.Locality;
import com.proptiger.data.model.Enquiry;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import org.jboss.logging.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 *
 * @author mukand
 */

public interface LocalityDao extends JpaRepository<Locality, Long>{
    
    @Query("SELECT COUNT(*) "
            + " FROM Locality L join L.enquiry E WHERE L.localityId=E.localityId AND "
            + " UNIX_TIMESTAMP(E.createdDate) >= (UNIX_TIMESTAMP() - ?1) AND "
            + " CASE ?2 WHEN 1 THEN L.localityId WHEN 2 THEN L.suburbId ELSE L.cityId END = ?3")
    public Integer findTotalEnquiryCountOnCityOrSubOrLoc(@Param Long timediff, @Param Long location_type, @Param Long location_id);
//    
    
    @Query("SELECT L.localityId, L.label, COUNT(*) AS enquiryCount"
            + " FROM Locality L join L.enquiry E WHERE L.localityId=E.localityId AND "
            + " UNIX_TIMESTAMP(E.createdDate) >= (UNIX_TIMESTAMP() - ?1) AND "
            + " CASE ?2 WHEN 1 THEN L.localityId WHEN 2 THEN L.suburbId ELSE L.cityId END = ?3 GROUP BY E.localityId ORDER BY enquiryCount DESC ")
    public List<Locality> findEnquiryCountOnCityOrSubOrLoc(@Param Long timediff, @Param Long location_type, @Param Long location_id);
    
    @Query("SELECT L.localityId, L.label, E.name, E.email, E.id"
            + " FROM Locality L join L.enquiry E where L.localityId = E.localityId and L.localityId=?1")
    public List<Locality> findEnquiryCountOnLoc(@Param Long localityId);//@Param("localityId") Integer localityId, @Param("months") Integer months);
     
}