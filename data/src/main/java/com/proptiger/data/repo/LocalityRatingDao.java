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

import com.proptiger.data.model.LocalityReview;
import com.proptiger.data.model.LocalityReview.LocalityAverageRatingCategory;

/**
 *
 * @author mukand
 */
@Repository
public interface LocalityRatingDao extends PagingAndSortingRepository<LocalityReview, Serializable> {
    
    @Query(" SELECT AVG(LR.overallRating), COUNT(LR.overallRating) FROM LocalityReview AS LR "
            + " WHERE LR.overallRating IS NOT NULL AND LR.localityId = ?1 ")
    public Object[] getAvgAndTotalRatingByLocalityId(int localityId);
    
    @Query(" SELECT LR.overallRating, COUNT(*) FROM LocalityReview AS LR WHERE "
    		+ "LR.overallRating IS NOT NULL  AND LR.localityId = ?1 GROUP BY LR.overallRating ORDER BY LR.overallRating DESC")
    public List<Object[]> getTotalUsersByRating(int localityId);  
    
    public LocalityReview findByUserIdAndLocalityId(Integer userId, Integer localityId);
    
    @Query(" SELECT NEW com.proptiger.data.model.LocalityReview$LocalityAverageRatingCategory(AVG(nullif(LR.overallRating, 0)), " +
    		" AVG(nullif(LR.location, 0))," +
    		" AVG(nullif(LR.safety, 0))," +
    		" AVG(nullif(LR.pubTrans, 0))," +
    		" AVG(nullif(LR.restShop, 0))," +
    		" AVG(nullif(LR.schools, 0))," +
    		" AVG(nullif(LR.parks, 0))," +
    		" AVG(nullif(LR.traffic, 0))," +
    		" AVG(nullif(LR.hospitals, 0))," +
    		" AVG(nullif(LR.civic, 0)) ) " +
    		" FROM LocalityReview AS LR " +
            " WHERE LR.localityId = ?1 ")
    public LocalityAverageRatingCategory getAvgRatingOfAmenitiesForLocality(Integer localityId);
}
