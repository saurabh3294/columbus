package com.proptiger.data.service;

import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Extends This abstarct class in test classes to initialize spring system
 * 
 * @author Rajeev Pandey
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring/applicationContext.xml")
public abstract class AbstractTest {

}
