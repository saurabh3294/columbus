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
	@Query("select I from Image as I " +
			" where " +
			" I.imageTypeId in ( " +
			" 	SELECT IT.id " +
			" 	FROM ObjectType AS O " +
			" 	, ImageType AS IT" + 
			" 	WHERE O.id = IT.objectTypeId AND O.type =  ?1 " +
			") AND I.objectId = ?2 AND I.active=1")
	public List<Image> getImagesForObject(String obj, long objectId);
	
	@Query("select I from Image as I " +
			" where " +
			" I.imageTypeId = ( " +
			" 	SELECT IT.id " +
			" 	FROM ObjectType AS O " +
			" 	, ImageType AS IT" + 
			" 	WHERE O.id = IT.objectTypeId AND O.type =  ?1 " +
			" 	AND IT.type =  ?2 " +
			") AND I.objectId = ?3 AND I.active=1")
	public List<Image> getImagesForObjectWithImageType(String obj, String type, long objectId);
	
	@Transactional
	@Modifying
	@Query("update Image set active = false where id = ?1")
	public void setActiveFalse(long imageId);
}
