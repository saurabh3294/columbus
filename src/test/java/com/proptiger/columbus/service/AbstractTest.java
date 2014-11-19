package com.proptiger.columbus.service;

import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.test.context.web.WebAppConfiguration;
import org.testng.annotations.Test;

import com.proptiger.core.config.WebMvcConfig;

/**
 * Extends This abstarct class in test classes to initialize spring system
 * 
 * @author Rajeev Pandey
 * 
 */
@Test
@ContextConfiguration(classes = WebMvcConfig.class)
@WebAppConfiguration
@PropertySource("classpath:test.properties")
public abstract class AbstractTest extends AbstractTestNGSpringContextTests {

}
