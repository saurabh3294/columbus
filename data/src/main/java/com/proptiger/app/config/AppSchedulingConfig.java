package com.proptiger.app.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Enable sceduling and could be used to enable dynamic scheduling.
 * 
 * @author Rajeev Pandey
 * 
 */
@Configuration
@EnableAsync
@EnableScheduling
public class AppSchedulingConfig {

}
