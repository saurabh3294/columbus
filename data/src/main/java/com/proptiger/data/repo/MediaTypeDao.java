package com.proptiger.data.repo;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.proptiger.data.model.MediaType;

/**
 * Dao object for media types
 * 
 * @author azi
 * 
 */

public interface MediaTypeDao extends PagingAndSortingRepository<MediaType, Integer> {
    public MediaType findByName(com.proptiger.data.enums.MediaType mediaTypeName);

    public Integer getMediaTypeIdFromMediaTypeName(com.proptiger.data.enums.MediaType mediaTypeName);
}