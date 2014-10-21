package com.proptiger.data.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proptiger.data.model.image.ImageQuality;
import com.proptiger.data.repo.ImageQualityDao;

@Service
public class ImageQualityService {

    @Autowired
    private ImageQualityDao imageQualityDao;

    public ImageQuality getQuality(int imageTypeId,int resolutionId) {

        return  imageQualityDao.findByImageTypeIdAndResolutionId(imageTypeId,resolutionId);
    }

}