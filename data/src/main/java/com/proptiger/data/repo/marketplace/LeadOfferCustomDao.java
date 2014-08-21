package com.proptiger.data.repo.marketplace;

import java.util.List;

import com.proptiger.data.model.marketplace.LeadOffer;
import com.proptiger.data.pojo.FIQLSelector;
import com.proptiger.data.pojo.response.PaginatedResponse;

public interface LeadOfferCustomDao {
    public PaginatedResponse<List<LeadOffer>> getLeadOffers(FIQLSelector selector);
}
