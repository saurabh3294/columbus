package com.proptiger.data.repo.marketplace;

import java.util.List;

import com.proptiger.core.pojo.FIQLSelector;
import com.proptiger.core.pojo.response.PaginatedResponse;
import com.proptiger.data.model.marketplace.LeadOffer;

public interface LeadOfferCustomDao {
    public PaginatedResponse<List<LeadOffer>> getLeadOffers(FIQLSelector selector);
    
    public PaginatedResponse<List<LeadOffer>> getLeadOffers(int agentId,List<Integer> statusIds, String dueDate, FIQLSelector selector);
}
