package com.proptiger.data.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.proptiger.core.enums.DataVersion;
import com.proptiger.core.model.cms.ListingPrice;
import com.proptiger.core.model.cms.ListingPrice.CustomCurrentListingPrice;

/**
 * 
 * @author azi
 * 
 */
public interface ListingPriceDao extends JpaRepository<ListingPrice, Integer> {
    public List<ListingPrice> getPrices(List<Integer> listingIds, DataVersion version);

    public List<CustomCurrentListingPrice> getPrices(List<Integer> listingIds);
}