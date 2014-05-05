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
public interface MediaDao extends PagingAndSortingRepository<Media, Integer>, CustomMediaDao {
    @Query("select M from Media as M JOIN FETCH M.objectMediaType OMT where M.contentHash = ?1 AND OMT.objectTypeId = ?2 AND M.isActive=1")
    public List<Media> findByContentHashAndObjectTypeId(String contentHash, Integer objectTypeId);
}
