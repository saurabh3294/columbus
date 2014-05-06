package com.proptiger.data.repo;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.proptiger.data.model.Media;

/**
 * Dao Interface for media model
 * 
 * @author azi
 * 
 */
public interface MediaDao extends PagingAndSortingRepository<Media, Integer>, MediaCustomDao {
    @Query("select M from Media as M JOIN FETCH M.objectMediaType OMT JOIN FETCH OMT.objectType OT where M.contentHash = ?1 AND OT.type = ?2 AND M.isActive=1")
    public List<Media> findByContentHashAndObjectType(String contentHash, String objectType);
}
