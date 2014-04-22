package com.proptiger.data.repo;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.springframework.beans.factory.annotation.Autowired;

import com.proptiger.data.model.LocalityReviewComments;
import com.proptiger.data.model.filter.AbstractQueryBuilder;
import com.proptiger.data.model.filter.JPAQueryBuilder;
import com.proptiger.data.pojo.FIQLSelector;
import com.proptiger.data.service.pojo.PaginatedResponse;

/**
 * Dao to find locality review for given selector
 * 
 * @author Rajeev Pandey
 * 
 */
public class LocalityReviewDaoImpl {

    @Autowired
    private EntityManagerFactory emf;

    /**
     * Get locality review for given selector and city id
     * 
     * @param cityId
     * @param selector
     * @return
     */
    public PaginatedResponse<List<LocalityReviewComments>> getLocalityReview(FIQLSelector selector) {
        EntityManager entityManager = emf.createEntityManager();
        AbstractQueryBuilder<LocalityReviewComments> builder = new JPAQueryBuilder<>(
                entityManager,
                LocalityReviewComments.class);
        builder.buildQuery(selector);
        PaginatedResponse<List<LocalityReviewComments>> paginatedResponse = new PaginatedResponse<>();
        paginatedResponse.setResults(builder.retrieveResults());
        paginatedResponse.setTotalCount(builder.retrieveCount());
        entityManager.close();
        return paginatedResponse;
    }
}
