package com.proptiger.data.service;

import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.proptiger.app.config.WebMvcConfig;

/**
 * Extends This abstarct class in test classes to initialize spring system
 * 
 * @author Rajeev Pandey
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes=WebMvcConfig.class)
@WebAppConfiguration
public abstract class AbstractTest {

}
