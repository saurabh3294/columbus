package com.proptiger.data.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.proptiger.data.model.image.Image;

@Repository
public interface ImageDao extends ImageCustomDao, JpaRepository<Image, Long> {
    @Query("select I from Image as I JOIN FETCH I.imageType IT JOIN FETCH IT.objectType O where O.type = ?1 and I.objectId IN ?2 AND I.active=1 ORDER BY I.id DESC")
	public List<Image> getImagesForObject(String obj, long objectId);
	
    @Query("select I from Image as I JOIN FETCH I.imageType IT JOIN FETCH IT.objectType O where O.type = ?1 and IT.type = ?2 and I.objectId IN ?3 AND I.active=1 ORDER BY I.id DESC")
	public List<Image> getImagesForObjectWithImageType(String obj, String type, long objectId);
	
	@Transactional
	@Modifying
	@Query("update Image set active = false where id = ?1")
	public void setActiveFalse(long imageId);

    @Query("select I from Image as I JOIN FETCH I.imageType IT JOIN FETCH IT.objectType O where O.type = ?1 and IT.type = ?2 and I.objectId IN ?3 AND I.active=1 ORDER BY I.id")
    public List<Image> getImagesForObjectIds(String objectType, String imageTypeStr, List<Long> objectIds);
}
