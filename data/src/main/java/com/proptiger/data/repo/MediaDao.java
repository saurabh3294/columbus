package com.proptiger.data.repo;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.proptiger.core.enums.MediaType;
import com.proptiger.core.model.proptiger.Media;

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

    @Query("SELECT M FROM Media AS M JOIN FETCH M.objectMediaType OMT JOIN FETCH OMT.objectType OT JOIN FETCH OMT.mediaType MT WHERE OT.type =?1 AND M.objectId IN ?2 AND M.active = 1 ORDER BY M.priority, M.id DESC")
    public List<Media> getMediaForObjectIds(String objectType, List<Integer> objectIds);

    @Query("SELECT M FROM Media AS M JOIN FETCH M.objectMediaType OMT JOIN FETCH OMT.objectType OT JOIN FETCH OMT.mediaType MT WHERE OT.type =?1 AND OMT.type = ?2 AND M.objectId IN ?3 AND M.active = 1 ORDER BY M.priority, M.id DESC")
    public List<Media> getMediaForObjectIdsWithMediaType(
            String objectType,
            String objectMediaType,
            List<Integer> objectIds);
}