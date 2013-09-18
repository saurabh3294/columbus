package com.proptiger.data.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

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
	
	public List<Image> getImages(DomainObject object, String type, long objId) {
		String objectType = object.getText();
		System.out.println("============================");
		System.out.println(objectType);
		System.out.println("============================");
		return imageDao.findAll();
	}
}
