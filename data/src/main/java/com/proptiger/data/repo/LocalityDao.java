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
    
    @Query("SELECT -1 AS localityId, '' AS label, COUNT(*) AS enquiryCount, 'TOTAL' AS queryType "
            + " FROM Locality L join L.enquiry E WHERE L.localityId=E.localityId AND "
            + " UNIX_TIMESTAMP(E.createdDate) >= (UNIX_TIMESTAMP() - ?1) AND "
            + " CASE ?2 WHEN 1 THEN L.localityId ELSE L.cityId END = ?3")
    public List<Locality> findEnquiryCountOnCityOrSubOrLoc(@Param Long timediff, @Param Long location_type, @Param Long location_id);
//    
    
    @Query("SELECT L.localityId, L.label, E.name, E.email, E.id"
            + " FROM Locality L join L.enquiry E where L.localityId = E.localityId and L.localityId=?1")
            public List<Locality> findEnquiryCountOnLoc(@Param Long localityId);//@Param("localityId") Integer localityId, @Param("months") Integer months);
    /* WHERE "
            + " L.LOCALITY_ID = E.LOCALITY_ID AND L.LOCALITY_ID = 100 AND YEAR(created_date) = YEAR(CURRENT_DATE - INTERVAL 1 MONTH) AND "
            + " MONTH(created_date) = MONTH(CURRENT_DATE - INTERVAL 1 MONTH) ")*/
    
}
/*@Query("select localityId, label from Locality where localityId > ?1 and localityId < ?2 "
                    + " UNION "
                    + " select localityId, label from Locality where localityId IN (?1, ?2, 1) ")*/


/*
 * + " UNION "
            + " SELECT L.localityId, L.label, COUNT(*) AS enquiryCount, 'GROUP' AS queryType"
            + " FROM Locality L join L.enquiry E WHERE L.localityId=E.localityId AND ?1 AND "
            + "  YEAR(createdDate) = YEAR(CURRENT_DATE - INTERVAL ?2 MONTH) AND "
            + "  MONTH(createdDate) = MONTH(CURRENT_DATE - INTERVAL ?2 MONTH) "
            + " ")
 * 
 */