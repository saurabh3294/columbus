package com.proptiger.data.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.proptiger.core.model.proptiger.Image;

@Repository
public interface ImageDao extends ImageCustomDao, JpaRepository<Image, Long> {
    @Query("select I from Image as I JOIN FETCH I.imageTypeObj IT JOIN FETCH IT.objectType O JOIN FETCH IT.mediaType MT where IT.mediaTypeId = 1 and O.type = ?1 and I.objectId IN ?2 AND I.active=1 ORDER BY IT.priority, IT.id, I.priority, I.takenAt DESC, I.id DESC")
    public List<Image> getImagesForObject(String obj, long objectId);

    @Query("select I from Image as I JOIN FETCH I.imageTypeObj IT JOIN FETCH IT.objectType O JOIN FETCH IT.mediaType MT where IT.mediaTypeId = 1 and O.type = ?1 and IT.type = ?2 and I.objectId IN ?3 AND I.active=1 ORDER BY IT.priority, IT.id, I.priority, I.takenAt DESC, I.id DESC")
    public List<Image> getImagesForObjectWithImageType(String obj, String type, long objectId);

    @Transactional
    @Modifying
    @Query("update Image set active = false where id = ?1")
    public void setActiveFalse(long imageId);

    @Query("select I from Image as I JOIN FETCH I.imageTypeObj IT JOIN FETCH IT.objectType O JOIN FETCH IT.mediaType MT where IT.mediaTypeId = 1 and O.type = ?1 and I.objectId IN ?2 AND I.active=1 ORDER BY IT.priority, IT.id, I.priority, I.takenAt DESC, I.id DESC")
    public List<Image> getImagesForObjectIds(String objectType, List<Long> objectIds);

    @Query("select I from Image as I JOIN FETCH I.imageTypeObj IT JOIN FETCH IT.objectType O JOIN FETCH IT.mediaType MT where IT.mediaTypeId = 1 and O.type = ?1 and IT.type = ?2 and I.objectId IN ?3 AND I.active=1 ORDER BY IT.priority, IT.id, I.priority, I.takenAt DESC, I.id DESC")
    public List<Image> getImagesForObjectIdsWithImageType(String objectType, String imageTypeStr, List<Long> objectIds);

    public List<Image> getImageOnHashAndObjectType(String originalHash, String objectType);
    
    @Query("SELECT I FROM Image I JOIN FETCH I.imageTypeObj IT JOIN FETCH IT.objectType O JOIN FETCH IT.mediaType MT, com.proptiger.core.model.cms.LandMark LM WHERE O.type = ?1 AND LM.id = I.objectId AND I.objectId IN ?2 AND I.active = 1 ORDER BY IT.priority, IT.id, LM.priority, I.priority")
    public List<Image> getLandMarkImages(String objectType, List<Long> objectIds);
}
