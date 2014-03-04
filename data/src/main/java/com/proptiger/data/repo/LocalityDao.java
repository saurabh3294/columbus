/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proptiger.data.repo;

import java.util.Date;
import java.util.List;

import org.jboss.logging.Param;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.proptiger.data.model.Locality;

/**
 * 
 * @author mukand
 * @author Rajeev Pandey
 */
@Repository
public interface LocalityDao extends PagingAndSortingRepository<Locality, Integer>, LocalityCustomDao {

    @Query("SELECT COUNT(*) " + " FROM Locality L join L.enquiry E WHERE L.localityId=E.localityId AND "
            + " E.createdDate >= ?1 AND "
            + " CASE ?2 WHEN 1 THEN L.cityId WHEN 2 THEN L.suburbId WHEN 3 THEN L.cityId END = ?3")
    public Long findTotalEnquiryCountOnCityOrSubOrLoc(
            @Param Date date,
            @Param Long location_type,
            @Param int location_id);

    @Query("SELECT L.localityId, L.label, COUNT(*) AS enquiryCount" + " FROM Locality L join L.enquiry E WHERE L.localityId=E.localityId AND "
            + " E.createdDate >= ?1 AND "
            + " CASE ?2 WHEN 1 THEN L.cityId WHEN 2 THEN L.suburbId WHEN 3 THEN L.cityId END = ?3 "
            + " GROUP BY E.localityId ORDER BY enquiryCount DESC ")
    public List<Object[]> findEnquiryCountOnCityOrSubOrLoc(
            @Param Date date,
            @Param Long location_type,
            @Param int location_id);

    @Query("SELECT L.localityId, L.label, COUNT(*) AS enquiryCount " + " FROM Locality L join L.enquiry E where L.localityId = E.localityId AND "
            + " E.createdDate >= ?1 AND "
            + " L.localityId=?2")
    public Object[] findEnquiryCountOnLoc(@Param Date date, @Param int localityId);

    @Query("Select L, AVG(LR.overallRating) as overallAvgRating from Locality L left join L.localityRatings LR " + " where L.localityId = LR.localityId AND (L.cityId=?1 OR L.suburbId=?2)"
            + " group by L.localityId having AVG(LR.overallRating) >= ?3 order by overallAvgRating DESC")
    public List<Object[]> getTopLocalityByCityIdOrSuburbIdAndRatingGreaterThan(
            Integer cityId,
            Integer suburbId,
            double rating,  Pageable pageable);

}
