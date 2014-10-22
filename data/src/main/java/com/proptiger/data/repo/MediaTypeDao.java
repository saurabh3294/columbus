package com.proptiger.data.repo;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.proptiger.core.model.proptiger.MediaType;

/**
 * Dao object for media types
 * 
 * @author azi
 * 
 */

public interface MediaTypeDao extends PagingAndSortingRepository<MediaType, Integer> {
    public MediaType findByName(com.proptiger.core.enums.MediaType mediaTypeName);

    public Integer getMediaTypeIdFromMediaTypeName(com.proptiger.core.enums.MediaType mediaTypeName);
}