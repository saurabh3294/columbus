package com.proptiger.data.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.proptiger.data.model.image.ImageResolutionModel;

public interface ImageResolutionDao extends JpaRepository<ImageResolutionModel, Integer>{
    
    public ImageResolutionModel findByLabel(String label);

}
