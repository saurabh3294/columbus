package com.proptiger.data.repo;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.proptiger.data.enums.MediaType;
import com.proptiger.data.model.Media;

/**
 * Dao Interface for media model
 * 
 * @author azi
 * 
 */
public interface MediaDao extends PagingAndSortingRepository<Media, Integer>, MediaCustomDao {
    @Query("select M from Media as M JOIN FETCH M.objectMediaType OMT JOIN FETCH OMT.objectType OT where M.contentHash = ?1 AND OT.type = ?2 AND M.active=1")
    public List<Media> findByContentHashAndObjectType(String contentHash, String objectType);

    @Query("select M from Media as M JOIN FETCH M.objectMediaType OMT JOIN FETCH OMT.objectType OT JOIN FETCH OMT.mediaType MT where M.objectId = ?1 AND OT.type = ?2 AND MT.name = ?3 AND M.active=1")
    public List<Media> findByObjectIdAndObjectTypeAndMediaType(Integer objectId, String objectType, MediaType MediaType);

    @Query("select M from Media as M JOIN FETCH M.objectMediaType OMT JOIN FETCH OMT.objectType OT JOIN FETCH OMT.mediaType MT where M.objectId = ?1 AND OT.type = ?2 AND MT.name = ?3 AND OMT.type = ?4 AND M.active=1")
    public List<Media> findByObjectIdAndObjectTypeAndMediaTypeAndObjectMediaType(
            Integer objectId,
            String objectType,
            MediaType MediaType,
            String objectMediaType);
}