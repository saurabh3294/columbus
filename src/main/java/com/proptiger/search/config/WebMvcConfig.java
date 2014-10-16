package com.proptiger.search.config;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.VelocityException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.jmx.export.annotation.AnnotationMBeanExporter;
import org.springframework.ui.velocity.VelocityEngineFactory;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.proptiger.core.init.CustomObjectMapper;
import com.proptiger.core.util.DateToStringConverter;
import com.proptiger.core.util.LongToDateConverter;
import com.proptiger.core.util.PropertyReader;
import com.proptiger.core.util.StringToDateConverter;

/**
 * Initialize application wide beans here.
 * @author Rajeev Pandey
 *
 */
@Configuration
@ComponentScan(basePackages = { "com.proptiger" })
@EnableAspectJAutoProxy
@PropertySource("classpath:application.properties")
public class WebMvcConfig extends WebMvcConfigurationSupport {

    @Autowired
    private PropertyReader propertyReader;

//    @Bean
//    public RequestResponseInterceptor createRequestResponseInterceptor() {
//        RequestResponseInterceptor interceptor = new RequestResponseInterceptor();
//        return interceptor;
//    }

//    @Override
//    public void addInterceptors(InterceptorRegistry registry) {
//        registry.addInterceptor(createRequestResponseInterceptor());
//    }

    @Override
    protected void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new StringToDateConverter());
        registry.addConverter(new DateToStringConverter());
        registry.addConverter(new LongToDateConverter());
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(messageConverter());
        addDefaultHttpMessageConverters(converters);
    }

    @Bean
    public MappingJackson2HttpMessageConverter messageConverter() {
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setObjectMapper(getObjectMapper());
        return converter;
    }

    @Bean
    public ObjectMapper getObjectMapper() {
        return new CustomObjectMapper();
    }

    @Bean(name = "velocityEngine")
    public VelocityEngine getVelocityEngine() throws VelocityException, IOException {
        VelocityEngineFactory factory = new VelocityEngineFactory();
        Properties props = new Properties();
        props.put("resource.loader", "class");
        props.put("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        factory.setVelocityProperties(props);
        return factory.createVelocityEngine();
    }

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertyPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }
    @Bean(name = "multipartResolver")
    public CommonsMultipartResolver getMultiPartResolver(){
        CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver();
        multipartResolver.setMaxUploadSize(104857600);
        return multipartResolver;
    }
    @Bean(name = "exporter")
    public AnnotationMBeanExporter createmBeanExporter(){
        return new AnnotationMBeanExporter();
    }
}