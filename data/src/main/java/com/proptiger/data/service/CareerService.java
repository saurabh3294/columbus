package com.proptiger.data.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proptiger.core.pojo.FIQLSelector;
import com.proptiger.core.pojo.response.PaginatedResponse;
import com.proptiger.data.model.JobDetail;
import com.proptiger.data.repo.CareerDao;

/**
 * @author Rajeev Pandey
 *
 */
@Service
public class CareerService {

    @Autowired
    private CareerDao careerDao;
    
    /**
     * Get open job details for given selector
     * @param selector
     * @return
     */
    public PaginatedResponse<List<JobDetail>> getJobDetails(FIQLSelector selector){
        return careerDao.getJobDetails(selector);
    }
}
