package com.proptiger.data.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proptiger.data.model.Testimonial;
import com.proptiger.data.pojo.FIQLSelector;
import com.proptiger.data.repo.TestimonialDao;
import com.proptiger.data.service.pojo.PaginatedResponse;

/**
 * @author Rajeev Pandey
 *
 */
@Service
public class TestimonialService {

    @Autowired
    private TestimonialDao testimonialDao;
    
    /**
     * Get testimonials for given selector
     * @param selector
     * @return
     */
    public PaginatedResponse<List<Testimonial>> getTestimonials(FIQLSelector selector){
        return testimonialDao.getTestimonials(selector);
    }
}
