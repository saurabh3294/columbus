package com.proptiger.data.repo;

import org.springframework.beans.factory.annotation.Autowired;

import com.proptiger.data.model.MediaType;

/**
 * 
 * @author azi
 * 
 */
public class MediaTypeDaoImpl {
    @Autowired
    private MediaTypeDao mediaTypeDao;

    public Integer getMediaTypeIdFromMediaTypeName(com.proptiger.data.enums.MediaType mediaTypeName) {
        MediaType media = mediaTypeDao.findByName(mediaTypeName);
        if (media != null) {
            return media.getId();
        }
        else {
            return null;
        }
    }
}