package com.proptiger.data.repo;

import java.util.List;

import javax.persistence.EntityManagerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.proptiger.data.model.JobDetail;
import com.proptiger.data.model.filter.AbstractQueryBuilder;
import com.proptiger.data.model.filter.JPAQueryBuilder;
import com.proptiger.data.pojo.FIQLSelector;
import com.proptiger.data.service.pojo.PaginatedResponse;

/**
 * @author Rajeev Pandey
 *
 */
@Component
public class CareerDao {

    @Autowired
    private EntityManagerFactory emf;
    
    /**
     * Get job details with default condition of status as 1 and jobs that are not deleted
     * @param selector
     * @return
     */
    public PaginatedResponse<List<JobDetail>> getJobDetails(FIQLSelector selector){
        if(selector == null){
            selector = new FIQLSelector();
        }
        AbstractQueryBuilder<JobDetail> builder = new JPAQueryBuilder<>(emf.createEntityManager(), JobDetail.class);
        selector.addAndConditionToFilter("status==1").addAndConditionToFilter("deletedFlag==0").addSortASC("department");
        builder.buildQuery(selector);
        PaginatedResponse<List<JobDetail>> paginatedResponse = new PaginatedResponse<>();
        paginatedResponse.setResults(builder.retrieveResults());
        paginatedResponse.setTotalCount(builder.retrieveCount());
        return paginatedResponse;
    }
}
