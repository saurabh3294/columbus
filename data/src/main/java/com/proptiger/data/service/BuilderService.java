/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proptiger.data.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proptiger.data.model.Builder;
import com.proptiger.data.model.DomainObject;
import com.proptiger.data.model.image.Image;
import com.proptiger.data.repo.BuilderDao;

/**
 * 
 * @author mukand
 */
@Service
public class BuilderService {
    @Autowired
    private BuilderDao builderDao;

    @Autowired
    private ImageService imageService;

    public Builder getBuilderDetailsByProjectId(int projectId) {
        Builder builder = builderDao.findByProjectId(projectId);
        List<Image> images = imageService.getImages(DomainObject.builder, "logo", builder.getId());
        if (images != null && !images.isEmpty()) {
            builder.setImageURL(images.get(0).getAbsolutePath());
        }

        return builder;
    }
    
    public Builder getBuilderInfo(Integer builderId){
    	Builder builder = builderDao.findOne(builderId);
    	
    	return builder;
    }
}
