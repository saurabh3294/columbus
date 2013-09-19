package com.proptiger.data.service;

import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.proptiger.data.model.DomainObject;
import com.proptiger.data.model.image.Image;
import com.proptiger.data.repo.ImageDao;

/**
 * @author yugal
 *
 */
@Service
public class ImageService {
	@Resource
	private ImageDao imageDao;
	
	/*
	 * Method to retrieve images
	 * */
	public List<Image> getImages(DomainObject object, String type, int objId) {
		if(type == null) {
			return imageDao.getImagesForObject(object.getText(), objId);			
		} else {
			return imageDao.getImagesForObjectWithImageType(object.getText(), type, objId);	
		}
	}
	
	public boolean isValidImage(MultipartFile file) {
		if(file.getSize() == 0) {
			return false;
		} else {
			return true;
		}
	}
	
	public void convertToJPG() {
	}
	
	public void optimizeJPG() {
	}
	
	public void makeProgresiveJPG() {
	}
	
	public void createWatermarkedCopy() {
	}
	
	public HashMap<String, String> getFileAttributes() {
		return new HashMap<String, String>();
	}
	
	public void uploadToS3() {
	}
}
