package com.proptiger.data.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.proptiger.data.model.Builder;
import com.proptiger.data.model.Locality;
import com.proptiger.data.model.Project;
import com.proptiger.data.model.ProjectDB;
import com.proptiger.data.model.Property;
import com.proptiger.data.model.enums.DomainObject;
import com.proptiger.data.model.image.Image;
import com.proptiger.data.repo.ImageDao;

@Service
public class ImageEnricher {

    @Autowired
    private ImageService imageService;

    @Autowired
    private ImageDao imageDao;

    public void setProjectsImages(List<Project> projects) {
    	if(projects == null)
    		return;
    	
        for (Project project : projects) {
            setProjectImages(project);
        }
    }

    public void setProjectImages(Project project) {
    	if(project == null)
    		return;
    	
        List<Image> images = imageService.getImages(DomainObject.project, null, project.getProjectId());
        List<Image> mainImages = imageService.getImages(DomainObject.project, "main", project.getProjectId());

        if (mainImages != null && !mainImages.isEmpty())
            project.setImageURL(mainImages.get(0).getAbsolutePath());

        project.setImages(images);

        setBuilderImages(project.getBuilder());
    }

    @Deprecated
    public void setProjectDBImages(ProjectDB project) {
        if (project == null)
            return;

        List<Image> images = imageService.getImages(DomainObject.project, null, project.getProjectId());
        List<Image> mainImages = imageService.getImages(DomainObject.project, "main", project.getProjectId());
        
        project.setImages(images);
        if (mainImages != null && !mainImages.isEmpty()) {
            project.setImageURL(mainImages.get(0).getAbsolutePath());
        }
    }

    public void setPropertiesImages(List<Property> properties) {
    	if(properties == null)
    		return;
    	
        for (Property property: properties) {
           setPropertyImages(property);
        }
    }

    public void setPropertyImages(Property property) {
    	if(property == null)
    		return;
    	
        List<Image> images = imageService.getImages(DomainObject.property, null, property.getPropertyId());
        property.setImages(images);
        setProjectImages(property.getProject());

    }

    public void setBuildersImages(List<Builder> builders) {
    	if(builders == null)
    		return;
    	
        for (Builder builder: builders) {
           setBuilderImages(builder);
        }
    }

    public void setBuilderImages(Builder builder) {
        if (builder == null)
            return;

        List<Image> images = imageService.getImages(DomainObject.builder, null, builder.getId());
        List<Image> logoImages = imageService.getImages(DomainObject.builder, "logo", builder.getId());
        if (logoImages != null && logoImages.size() > 0)
            builder.setImageURL(logoImages.get(0).getAbsolutePath());
    }

    public void setLocalitiesImages(List<Locality> localities) {
    	if(localities == null)
    		return;
    	
        Locality locality;
        for (int i = 0; i < localities.size(); i++) {
            locality = localities.get(i);
            setLocalityImages(locality, null);
        }
    }

    public void setLocalityImages(Locality locality, Integer numberOfImages) {
    	if(locality == null)
    		return;
    	
        List<Image> images = imageService.getImages(DomainObject.locality, null, locality.getLocalityId());
        if (images != null && images.size() > 0) {
            locality.setImageCount(images.size());

            if (numberOfImages == null || numberOfImages < 0 || numberOfImages > images.size())
                numberOfImages = images.size();
            
            locality.setImages(images.subList(0, numberOfImages));
        }
    }

}
