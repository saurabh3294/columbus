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

import com.proptiger.core.model.cms.Locality;

/**
 * 
 * @author mukand
 * @author Rajeev Pandey
 */
@Repository
public interface LocalityDao extends PagingAndSortingRepository<Locality, Integer>, LocalityCustomDao {

    @Query("SELECT COUNT(*) " + " FROM Locality L join L.suburb S join L.enquiry E WHERE L.suburbId=S.id AND L.localityId=E.localityId AND "
            + " E.createdDate >= ?1 AND "
            + " CASE ?2 WHEN 1 THEN S.cityId WHEN 2 THEN L.suburbId WHEN 3 THEN S.cityId END = ?3")
    public Long findTotalEnquiryCountOnCityOrSubOrLoc(
            @Param Date date,
            @Param Long location_type,
            @Param int location_id);

    @Query("SELECT L.localityId, L.label, COUNT(*) AS enquiryCount" + " FROM Locality L join L.suburb S join L.enquiry E WHERE L.suburbId=S.id AND L.localityId=E.localityId AND "
            + " E.createdDate >= ?1 AND "
            + " CASE ?2 WHEN 1 THEN S.cityId WHEN 2 THEN L.suburbId WHEN 3 THEN S.cityId END = ?3 "
            + " GROUP BY E.localityId ORDER BY enquiryCount DESC ")
    public List<Object[]> findEnquiryCountOnCityOrSubOrLoc(
            @Param Date date,
            @Param Long location_type,
            @Param int location_id);

    @Query("SELECT L.localityId, L.label, COUNT(*) AS enquiryCount " + " FROM Locality L join L.enquiry E where L.localityId = E.localityId AND "
            + " E.createdDate >= ?1 AND "
            + " L.localityId=?2")
    public Object[] findEnquiryCountOnLoc(@Param Date date, @Param int localityId);

    @Query("Select L.localityId, AVG(LR.overallRating) as overallAvgRating from Locality L join L.suburb S left join L.localityRatings LR " +
            " where L.suburbId=S.id AND L.localityId = LR.localityId AND (S.cityId=?1 OR L.suburbId=?2) AND L.status = 'Active' AND (L.localityId not in (?4) or ?4 is NULL) "
            + " group by L.localityId having AVG(LR.overallRating) >= ?3 order by overallAvgRating DESC, COUNT(LR.overallRating) DESC")
    public List<Object[]> getTopLocalityByCityIdOrSuburbIdAndRatingGreaterThan(
            Integer cityId,
            Integer suburbId,
            double rating,
            Integer excludeLocalityId,
            Pageable pageable);

    @Query("SELECT L FROM Locality L left join fetch L.suburb S left join fetch S.city where L.localityId = ?1")
    public Locality getLocalityOnId(Integer localityId);

    @Query("SELECT L FROM Locality L left join fetch L.suburb S left join fetch S.city C where L.label = ?1 AND C.label = ?2")
    public Locality getLocalityOnLocAndCity(String localityName, String cityName);

}
