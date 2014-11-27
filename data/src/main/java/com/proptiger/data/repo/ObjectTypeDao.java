package com.proptiger.data.repo;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.proptiger.core.model.proptiger.ObjectType;

/**
 * 
 * @author azi
 * 
 */
public interface ObjectTypeDao extends PagingAndSortingRepository<ObjectType, Integer> {
    public ObjectType findByType(String type);

    public Integer getObjectTypeIdByType(String type);
}