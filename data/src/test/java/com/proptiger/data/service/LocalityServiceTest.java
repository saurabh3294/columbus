package com.proptiger.data.service;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.proptiger.data.model.Locality;

public class LocalityServiceTest extends AbstractTest{
    @Autowired
    private LocalityService localityService;

    @Test
    public void testLocalityService() {
        Locality locality = localityService.getLocality(50186);
        Assert.assertEquals("Locality Name Matched", "Electronics City", locality.getLabel());
    }
}
