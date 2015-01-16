package com.proptiger.data.repo.marketplace;

import java.util.List;

import com.proptiger.core.model.cms.Listing;
import com.proptiger.core.pojo.FIQLSelector;
import com.proptiger.core.pojo.response.PaginatedResponse;

/**
 * @author Rajeev Pandey
 *
 */
public interface ListingCustomDao {

    public PaginatedResponse<List<Listing>> getListings(FIQLSelector selector);
}
