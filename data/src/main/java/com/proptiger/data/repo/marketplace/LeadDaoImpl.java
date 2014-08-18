package com.proptiger.data.repo.marketplace;

import java.util.List;

import javax.persistence.EntityManagerFactory;

import org.springframework.beans.factory.annotation.Autowired;

import com.proptiger.data.model.filter.AbstractQueryBuilder;
import com.proptiger.data.model.filter.JPAQueryBuilder;
import com.proptiger.data.model.marketplace.LeadOffer;
import com.proptiger.data.pojo.FIQLSelector;
import com.proptiger.data.pojo.response.PaginatedResponse;

public class LeadDaoImpl {

    @Autowired
    private LeadDao leadDao;
    
    @Autowired
    private EntityManagerFactory emf;
   
    public PaginatedResponse<List<LeadOffer>> getLeadOffers(FIQLSelector selector) {
        if (selector == null) {
            selector = new FIQLSelector();
        }
        AbstractQueryBuilder<LeadOffer> lead = new JPAQueryBuilder<>(emf.createEntityManager(), LeadOffer.class);
        lead.buildQuery(selector);
        PaginatedResponse<List<LeadOffer>> paginatedResponse = new PaginatedResponse<>();
        paginatedResponse.setResults(lead.retrieveResults());
        paginatedResponse.setTotalCount(lead.retrieveCount());
        return paginatedResponse;
    }
}
