package com.proptiger.data.repo;


import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.proptiger.data.model.ReviewComments;

/**
 * Dao class to handle CRUD operation for Locality review
 * 
 * @author Rajeev Pandey
 *
 */
@Repository
public interface LocalityReviewDao extends PagingAndSortingRepository<ReviewComments, Long>{

    @Query("SELECT COUNT(*) FROM ReviewComments WHERE Status = '1' AND localityId = ?1")
    public Long getTotalReviewsByLocalityId(int localityId);
    
    @Query("SELECT R.review , R.reviewLabel, U.username, R.commenttime FROM ReviewComments AS R left join"
            + "  R.forumUser as U WHERE R.status = '1' AND R.localityId = ?1 "
            + " ORDER BY R.commenttime DESC ")
    public List<Object> getReviewCommentsByLocalityId(int localityId, Pageable pageable);
    
}
