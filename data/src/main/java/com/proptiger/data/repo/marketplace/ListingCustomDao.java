package com.proptiger.data.repo.marketplace;

import java.util.List;

import com.proptiger.data.model.Listing;
import com.proptiger.data.pojo.FIQLSelector;
import com.proptiger.data.pojo.response.PaginatedResponse;

/**
 * @author Rajeev Pandey
 *
 */
public interface ListingCustomDao {

    public PaginatedResponse<List<Listing>> getListings(FIQLSelector selector);
}
