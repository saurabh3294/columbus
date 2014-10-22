package com.proptiger.data.service;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proptiger.core.util.PropertyReader;
import com.proptiger.data.model.Testimonial;
import com.proptiger.data.pojo.FIQLSelector;
import com.proptiger.data.pojo.response.PaginatedResponse;
import com.proptiger.data.repo.TestimonialDao;

/**
 * @author Rajeev Pandey
 *
 */
@Service
public class TestimonialService {
    
    public static String cdnImageUrl;
    
    @Autowired
    private TestimonialDao testimonialDao;
    
    @Autowired
    private PropertyReader reader;
    
    @PostConstruct
    private void init() {
       cdnImageUrl = reader.getRequiredProperty("cdn.image.url");
    }
    
    /**
     * Get testimonials for given selector
     * @param selector
     * @return
     */
    public PaginatedResponse<List<Testimonial>> getTestimonials(FIQLSelector selector){
        return testimonialDao.getTestimonials(selector);
    }
}
