package com.proptiger.data.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proptiger.data.model.image.ImageResolutionModel;
import com.proptiger.data.repo.ImageResolutionDao;

@Service
public class ImageResolutionService {

    @Autowired
    private ImageResolutionDao imageResolutionDao;

    public Integer getResolutionId(String resolutionLabel) {

        ImageResolutionModel imageResolutionModel = imageResolutionDao.findByLabel(resolutionLabel);

        Integer resolutionId = null;

        if (imageResolutionModel != null) {
            resolutionId = imageResolutionModel.getId();
        }
        return resolutionId;

    }

}
