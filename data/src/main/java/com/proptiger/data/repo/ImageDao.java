package com.proptiger.data.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.proptiger.data.model.image.Image;

@Repository
public interface ImageDao extends JpaRepository<Image, Integer>, ImageCustomDao {
	@Query("select I from Image as I " +
			" where " +
			" I.imageTypeId in ( " +
			" 	SELECT IT.id " +
			" 	FROM ObjectType AS O " +
			" 	, ImageType AS IT" + 
			" 	WHERE O.id = IT.objectTypeId AND O.type =  ?1 " +
			") AND I.objectId = ?2")
	public List<Image> getImagesForObject(String obj, long objectId);
	
	@Query("select I from Image as I " +
			" where " +
			" I.imageTypeId = ( " +
			" 	SELECT IT.id " +
			" 	FROM ObjectType AS O " +
			" 	, ImageType AS IT" + 
			" 	WHERE O.id = IT.objectTypeId AND O.type =  ?1 " +
			" 	AND IT.type =  ?2 " +
			") AND I.objectId = ?3")
	public List<Image> getImagesForObjectWithImageType(String obj, String type, long objectId);
}
