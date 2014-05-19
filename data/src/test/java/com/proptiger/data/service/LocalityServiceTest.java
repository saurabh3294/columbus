package com.proptiger.data.service;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.proptiger.data.model.Locality;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring/applicationContext.xml")
public class LocalityServiceTest {
    @Autowired
    private LocalityService localityService;

    @Test
    public void testLocalityService() {
        Locality locality = localityService.getLocality(50186);
        Assert.assertEquals(locality.getLabel(), "Electronic City", "Locality Name Matched");
    }
}
