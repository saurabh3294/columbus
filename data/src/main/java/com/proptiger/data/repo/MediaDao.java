package com.proptiger.data.repo;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.proptiger.data.model.Media;

/**
 * Dao Interface for media model
 * 
 * @author azi
 * 
 */
public interface MediaDao extends PagingAndSortingRepository<Media, Integer>, CustomMediaDao {

}
