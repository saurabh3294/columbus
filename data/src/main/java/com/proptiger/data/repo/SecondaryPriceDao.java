package com.proptiger.data.repo;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.proptiger.data.model.SecondaryPrice;

/**
 * 
 * @author azi
 * 
 */

@Repository
public interface SecondaryPriceDao extends PagingAndSortingRepository<SecondaryPrice, Integer> {
}
