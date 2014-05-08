package com.proptiger.data.repo;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.proptiger.data.model.ListingPrice;

/**
 * 
 * @author azi
 * 
 */
public interface ListingPriceDao extends PagingAndSortingRepository<ListingPrice, Integer> {
}