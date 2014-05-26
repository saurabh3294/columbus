package com.proptiger.data.repo;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.proptiger.data.enums.DataVersion;
import com.proptiger.data.model.ListingPrice;
import com.proptiger.data.model.ListingPrice.CustomCurrentListingPrice;

/**
 * 
 * @author azi
 * 
 */
public interface ListingPriceDao extends PagingAndSortingRepository<ListingPrice, Integer> {
    public List<ListingPrice> getPrices(List<Integer> listingIds, DataVersion version);

    public List<CustomCurrentListingPrice> getPrices(List<Integer> listingIds);
}