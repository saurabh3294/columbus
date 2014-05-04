package com.proptiger.data.repo;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.proptiger.data.model.image.ObjectMediaType;

@Repository
public interface ObjectMediaTypeDao extends PagingAndSortingRepository<ObjectMediaType, Integer> {
    public List<ObjectMediaType> findByMediaTypeIdAndObjectTypeIdAndType(
            Integer mediaTypeId,
            Integer objectTypeId,
            String type);
}
