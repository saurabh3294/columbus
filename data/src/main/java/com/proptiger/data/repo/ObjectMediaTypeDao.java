package com.proptiger.data.repo;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.proptiger.data.model.image.ObjectMediaType;

/**
 * 
 * @author azi
 * 
 */
public interface ObjectMediaTypeDao extends PagingAndSortingRepository<ObjectMediaType, Integer> {
    public ObjectMediaType findByMediaTypeIdAndObjectTypeIdAndType(
            Integer mediaTypeId,
            Integer objectTypeId,
            String type);
}
