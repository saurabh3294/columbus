package com.proptiger.data.repo;

import java.util.List;

import javax.persistence.EntityManagerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.proptiger.data.model.Testimonial;
import com.proptiger.data.model.filter.AbstractQueryBuilder;
import com.proptiger.data.model.filter.JPAQueryBuilder;
import com.proptiger.data.pojo.FIQLSelector;
import com.proptiger.data.pojo.response.PaginatedResponse;

/**
 * @author Rajeev Pandey
 *
 */
@Component
public class TestimonialDao{
    @Autowired
    private EntityManagerFactory emf;
    
    /**
     * Get testimonials for given selector
     * @param selector
     * @return
     */
    public PaginatedResponse<List<Testimonial>> getTestimonials(FIQLSelector selector){
        if(selector == null){
            selector = new FIQLSelector();
        }
        AbstractQueryBuilder<Testimonial> builder = new JPAQueryBuilder<>(emf.createEntityManager(), Testimonial.class);
        selector.addAndConditionToFilter("status==1");
        selector.addSortDESC("createdDate");
        builder.buildQuery(selector);
        PaginatedResponse<List<Testimonial>> paginatedResponse = new PaginatedResponse<>();
        paginatedResponse.setResults(builder.retrieveResults());
        paginatedResponse.setTotalCount(builder.retrieveCount());
        return paginatedResponse;
    }
}
