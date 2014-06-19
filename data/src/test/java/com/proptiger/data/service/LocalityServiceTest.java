package com.proptiger.data.service;

import static org.testng.AssertJUnit.assertEquals;

import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Test;

import com.proptiger.data.model.Locality;

public class LocalityServiceTest extends AbstractTest{
    @Autowired
    private LocalityService localityService;

    @Test
    public void testLocalityService() {
        Locality locality = localityService.getLocality(50186);
        assertEquals("Locality Name Matched", "Electronics City", locality.getLabel());
    }
}
