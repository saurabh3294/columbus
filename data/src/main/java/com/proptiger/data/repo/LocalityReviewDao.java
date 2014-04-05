package com.proptiger.data.repo;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.proptiger.data.model.LocalityReviewComments;
import com.proptiger.data.model.LocalityReviewComments.LocalityReviewCustomDetail;

/**
 * Dao class to handle CRUD operation for Locality review
 * 
 * @author Rajeev Pandey
 * 
 */
@Repository
public interface LocalityReviewDao extends PagingAndSortingRepository<LocalityReviewComments, Long>, LocalityReviewCustomDao{

    @Query("SELECT COUNT(*) FROM LocalityReviewComments WHERE Status = '1' AND localityId = ?1")
    public Long getTotalReviewsByLocalityId(int localityId);

    @Query("SELECT NEW com.proptiger.data.model.LocalityReviewComments$LocalityReviewCustomDetail(R.review , R.reviewLabel, U.username, R.commenttime, R.userName)" + " FROM LocalityReviewComments AS R left join"
            + "  R.forumUser as U WHERE R.status = '1' AND R.localityId = ?1 "
            + " ORDER BY R.commenttime DESC ")
    public List<LocalityReviewCustomDetail> getReviewCommentsByLocalityId(int localityId, Pageable pageable);

    @Query("SELECT R.localityId FROM LocalityReviewComments AS R, Locality AS L WHERE R.localityId = L.localityId AND " + " CASE ?1 WHEN 1 THEN L.suburb.cityId WHEN 2 THEN L.suburbId END = ?2 "
            + " AND L.isActive = 1 AND R.status = '1' "
            + " GROUP BY R.localityId HAVING COUNT(*) > ?3 ORDER BY COUNT(*) DESC , L.priority ASC")
    public List<Integer> getTopReviewLocalitiesOnSuburbOrCity(
            int locationType,
            int locationId,
            long minCount,
            Pageable pageable);

    @Query("SELECT R.localityId FROM LocalityReviewComments AS R, Locality L WHERE R.localityId = L.localityId AND R.localityId IN (?1) AND R.status = '1' " + " GROUP BY R.localityId HAVING COUNT(*) > ?2 ORDER BY COUNT(*) DESC, L.priority ASC ")
    public List<Integer> getTopReviewNearLocalitiesOnLocality(
            List<Integer> locationIds,
            long minCount,
            Pageable pageable);

    public LocalityReviewComments getByLocalityIdAndUserId(Integer localityId, Integer userId);
}
