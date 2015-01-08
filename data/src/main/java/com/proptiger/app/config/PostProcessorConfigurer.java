package com.proptiger.app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PostProcessorConfigurer {
    
    @Bean
    public static CustomBeanPostProcessor createPostProcessor(){
        return new CustomBeanPostProcessor();
    }
}
