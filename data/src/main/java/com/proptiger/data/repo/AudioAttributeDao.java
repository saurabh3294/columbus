package com.proptiger.data.repo;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.proptiger.data.model.AudioAttributes;

public interface AudioAttributeDao extends PagingAndSortingRepository<AudioAttributes, Integer> {
    

}
