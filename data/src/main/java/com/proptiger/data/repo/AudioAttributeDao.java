package com.proptiger.data.repo;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.proptiger.core.model.proptiger.AudioAttributes;

public interface AudioAttributeDao extends PagingAndSortingRepository<AudioAttributes, Integer> {
    

}
