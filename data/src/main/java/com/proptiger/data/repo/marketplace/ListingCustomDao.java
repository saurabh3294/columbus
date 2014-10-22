package com.proptiger.data.repo.marketplace;

import java.util.List;

import com.proptiger.core.enums.DataVersion;
import com.proptiger.core.enums.Status;
import com.proptiger.core.model.cms.Listing;
import com.proptiger.data.pojo.FIQLSelector;
import com.proptiger.data.pojo.response.PaginatedResponse;

/**
 * @author Rajeev Pandey
 *
 */
public interface ListingCustomDao {

    public PaginatedResponse<List<Listing>> getListings(FIQLSelector selector);
    public List<Listing> findListings(Integer userId, DataVersion dataVersion, Status status, FIQLSelector selector);
}
