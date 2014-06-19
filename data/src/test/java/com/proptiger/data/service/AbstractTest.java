package com.proptiger.data.service;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.test.context.web.WebAppConfiguration;
import org.testng.annotations.Test;

import com.proptiger.app.config.WebMvcConfig;

/**
 * Extends This abstarct class in test classes to initialize spring system
 * 
 * @author Rajeev Pandey
 * 
 */
@Test
@ContextConfiguration(classes=WebMvcConfig.class)
@WebAppConfiguration
public abstract class AbstractTest extends AbstractTestNGSpringContextTests {

}
