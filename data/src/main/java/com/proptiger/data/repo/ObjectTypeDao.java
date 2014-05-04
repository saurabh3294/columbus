package com.proptiger.data.repo;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.proptiger.data.model.ObjectType;

@Repository
public interface ObjectTypeDao extends PagingAndSortingRepository<ObjectType, Integer> {
    public ObjectType findByType(String type);
}
