    /*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proptiger.data.repo;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;

import org.jboss.logging.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.proptiger.data.model.City;
import com.proptiger.data.model.Locality;
import com.proptiger.data.model.filter.MySqlQueryBuilder;

/**
 *
 * @author mukand
 */
    public interface LocalityDao extends PagingAndSortingRepository<Locality, Integer>, LocalityCustomDao {
    
    @Query("SELECT COUNT(*) "
            + " FROM Locality L join L.enquiry E WHERE L.localityId=E.localityId AND "
            + " UNIX_TIMESTAMP(E.createdDate) >= (UNIX_TIMESTAMP() - ?1) AND "
            + " CASE ?2 WHEN 1 THEN L.cityId WHEN 2 THEN L.suburbId WHEN 3 THEN L.cityId END = ?3")
    public Long findTotalEnquiryCountOnCityOrSubOrLoc(@Param Long timediff, @Param Long location_type, @Param int location_id);
   
    
    @Query("SELECT L.localityId, L.label, COUNT(*) AS enquiryCount"
            + " FROM Locality L join L.enquiry E WHERE L.localityId=E.localityId AND "
            + " UNIX_TIMESTAMP(E.createdDate) >= (UNIX_TIMESTAMP() - ?1) AND "
            + " CASE ?2 WHEN 1 THEN L.cityId WHEN 2 THEN L.suburbId WHEN 3 THEN L.cityId END = ?3 "
            + " GROUP BY E.localityId ORDER BY enquiryCount DESC ")
    public List<Object[]> findEnquiryCountOnCityOrSubOrLoc(@Param Long timediff, @Param Long location_type, @Param int location_id);
    
    @Query("SELECT L.localityId, L.label, COUNT(*) AS enquiryCount "
            + " FROM Locality L join L.enquiry E where L.localityId = E.localityId AND "
            + " UNIX_TIMESTAMP(E.createdDate) >= (UNIX_TIMESTAMP() - ?1) AND "
            + " L.localityId=?2")
    public Object[] findEnquiryCountOnLoc(@Param Long timediff, @Param int localityId);
    
    public Page<Locality> findByCityIdAndIsActiveAndDeletedFlagOrderByPriorityDesc(int cityId, boolean active, boolean deletedFlag, Pageable pageable);
    
    public Page<Locality> findByLocalityIdInAndIsActiveAndDeletedFlagOrderByPriorityDescLabelAsc(List<Integer> localityIds, boolean active, boolean deletedFlag, Pageable pageable);
}
