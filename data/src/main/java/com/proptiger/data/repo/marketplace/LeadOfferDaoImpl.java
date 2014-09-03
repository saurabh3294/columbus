package com.proptiger.data.repo.marketplace;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.springframework.beans.factory.annotation.Autowired;

import com.proptiger.data.model.filter.AbstractQueryBuilder;
import com.proptiger.data.model.filter.JPAQueryBuilder;
import com.proptiger.data.model.marketplace.LeadOffer;
import com.proptiger.data.pojo.FIQLSelector;
import com.proptiger.data.pojo.response.PaginatedResponse;

/**
 * @author Rajeev Pandey
 *
 */
public class LeadOfferDaoImpl {

    @Autowired
    private EntityManagerFactory emf;
    
    public PaginatedResponse<List<LeadOffer>> getLeadOffers(FIQLSelector selector) {
        EntityManager em = emf.createEntityManager();
        AbstractQueryBuilder<LeadOffer> leadOffer = new JPAQueryBuilder<>(em, LeadOffer.class);
        leadOffer.buildQuery(selector);
        PaginatedResponse<List<LeadOffer>> paginatedResponse = new PaginatedResponse<>();
        paginatedResponse.setResults(leadOffer.retrieveResults());
        paginatedResponse.setTotalCount(leadOffer.retrieveCount());
        em.close();
        return paginatedResponse;
    }
    
}
