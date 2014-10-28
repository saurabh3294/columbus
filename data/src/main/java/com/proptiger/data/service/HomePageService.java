package com.proptiger.data.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proptiger.core.pojo.FIQLSelector;

@Service
public class HomePageService {
    @Autowired
    private PropertyService propertyService;

    public Long getNoOfProperties() {
        FIQLSelector selector = new FIQLSelector().setRows(0);
        return propertyService.getProperties(selector).getTotalCount();
    }
}
