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
    public MediaType findByName(String mediaTypeName);

    public Integer getMediaTypeIdFromMediaTypeName(String mediaTypeName);
}