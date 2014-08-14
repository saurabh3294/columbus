package com.proptiger.data.repo.marketplace;

import java.util.List;

import com.proptiger.data.model.marketplace.Lead;
import com.proptiger.data.pojo.FIQLSelector;
import com.proptiger.data.pojo.response.PaginatedResponse;


public interface LeadCustomDao {

    public PaginatedResponse<List<Lead>> getLeadDetailsAfterFilter(FIQLSelector selector, int agentId);
    
}
