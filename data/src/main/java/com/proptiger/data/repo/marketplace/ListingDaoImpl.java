package com.proptiger.data.repo.marketplace;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.springframework.beans.factory.annotation.Autowired;

import com.proptiger.data.model.Listing;
import com.proptiger.data.model.filter.AbstractQueryBuilder;
import com.proptiger.data.model.filter.JPAQueryBuilder;
import com.proptiger.data.pojo.FIQLSelector;
import com.proptiger.data.pojo.response.PaginatedResponse;

/**
 * @author Rajeev Pandey
 *
 */
public class ListingDaoImpl {

    @Autowired
    private EntityManagerFactory emf;

    public PaginatedResponse<List<Listing>> getListings(FIQLSelector selector) {
        EntityManager em = emf.createEntityManager();
        PaginatedResponse<List<Listing>> paginatedResponse = new PaginatedResponse<>();
        AbstractQueryBuilder<Listing> builder = new JPAQueryBuilder<>(em, Listing.class);
        builder.buildQuery(selector);
        paginatedResponse.setResults(builder.retrieveResults());
        paginatedResponse.setTotalCount(builder.retrieveCount());
        em.close();
        return paginatedResponse;

    }
}
