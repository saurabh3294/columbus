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
    
}
