package com.proptiger.data.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.proptiger.data.model.Builder;
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
	
	public void setProjectsImages(String imageTypeStr, List<Project> projects){
			
		Project project;
		for(int i=0; i<projects.size(); i++){
			project = projects.get(i);
			setProjectImages(imageTypeStr, project);
		}
	}
	
	public void setProjectImages(String imageTypeStr, Project project){
		List<Image> images = imageService.getImages( DomainObject.project, imageTypeStr, project.getProjectId() );
		project.setImages(images);
		 if (images != null && !images.isEmpty()) {
                project.setImageURL(images.get(0).getAbsolutePath());
            }
	}
	
	@Deprecated
	public void setProjectDBImages(String imageTypeStr, ProjectDB project){
		List<Image> images = imageService.getImages( DomainObject.project, imageTypeStr, project.getProjectId() );
		project.setImages(images);
		 if (images != null && !images.isEmpty()) {
                project.setImageURL(images.get(0).getAbsolutePath());
            }
	}
	
	public void setPropertiesImages(String imageTypeStr, List<Property> properties){
		Property property;
		for(int i=0; i<properties.size(); i++){
			property = properties.get(i);
			setPropertyImages(imageTypeStr, property);
		}
	}
	
	public void setPropertyImages(String imageTypeStr, Property property){
		List<Image> images = imageService.getImages( DomainObject.property, imageTypeStr, property.getPropertyId() );
		property.setImages(images);
		setProjectImages("main", property.getProject());
		setBuilderImages("logo", property.getProject().getBuilder());
		property.getProject().setImages(null);
	}
	
	public void setBuildersImages(String imageTypeStr, List<Builder> builders){
		Builder builder;
		for(int i=0; i<builders.size(); i++){
			builder = builders.get(i);
			setBuilderImages(imageTypeStr, builder);
		}
	}
	
	public void setBuilderImages(String imageTypeStr, Builder builder){
		List<Image> images = imageService.getImages( DomainObject.builder, imageTypeStr, builder.getId() );
		if(images!=null && images.size() > 0)
			builder.setImageURL(images.get(0).getAbsolutePath());
	}
	
}
