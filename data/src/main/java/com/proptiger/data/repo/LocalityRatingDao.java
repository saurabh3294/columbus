/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proptiger.data.repo;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.proptiger.core.model.proptiger.LocalityRatings;
import com.proptiger.core.model.proptiger.LocalityRatings.LocalityAverageRatingByCategory;
import com.proptiger.core.model.proptiger.LocalityRatings.LocalityRatingUserCount;

/**
 * 
 * @author mukand
 * @author Rajeev Pandey
 */
@Repository
public interface LocalityRatingDao extends PagingAndSortingRepository<LocalityRatings, Serializable> {

    @Query(" SELECT NEW com.proptiger.core.model.proptiger.LocalityRatings$LocalityRatingUserCount(LR.overallRating, COUNT(*)) " + " FROM LocalityRatings AS LR WHERE "
            + " LR.overallRating IS NOT NULL  AND LR.localityId = ?1 AND LR.overallRating > 0 GROUP BY LR.overallRating ORDER BY LR.overallRating DESC")
    public List<LocalityRatingUserCount> getTotalUsersByRating(int localityId);

    public LocalityRatings findByUserIdAndLocalityId(Integer userId, Integer localityId);

    @Query(" SELECT NEW com.proptiger.core.model.proptiger.LocalityRatings$LocalityAverageRatingByCategory(AVG(nullif(LR.overallRating, 0)), " + " AVG(nullif(LR.location, 0)),"
            + " AVG(nullif(LR.safety, 0)),"
            + " AVG(nullif(LR.pubTrans, 0)),"
            + " AVG(nullif(LR.restShop, 0)),"
            + " AVG(nullif(LR.schools, 0)),"
            + " AVG(nullif(LR.parks, 0)),"
            + " AVG(nullif(LR.traffic, 0)),"
            + " AVG(nullif(LR.hospitals, 0)),"
            + " AVG(nullif(LR.civic, 0)) ) "
            + " FROM LocalityRatings AS LR "
            + " WHERE LR.localityId = ?1 ")
    public LocalityAverageRatingByCategory getAvgRatingOfAmenitiesForLocality(Integer localityId);
    
    @Query(" SELECT NEW com.proptiger.core.model.proptiger.LocalityRatings$LocalityAverageRatingByCategory(AVG(nullif(LR.overallRating, 0)), " + " AVG(nullif(LR.location, 0)),"
            + " AVG(nullif(LR.safety, 0)),"
            + " AVG(nullif(LR.pubTrans, 0)),"
            + " AVG(nullif(LR.restShop, 0)),"
            + " AVG(nullif(LR.schools, 0)),"
            + " AVG(nullif(LR.parks, 0)),"
            + " AVG(nullif(LR.traffic, 0)),"
            + " AVG(nullif(LR.hospitals, 0)),"
            + " AVG(nullif(LR.civic, 0)) ) "
            + " FROM LocalityRatings AS LR "
            + " WHERE LR.locality.suburbId = ?1 ")
    public LocalityAverageRatingByCategory getAvgRatingOfAmenitiesForSuburb(Integer suburbId);
}
