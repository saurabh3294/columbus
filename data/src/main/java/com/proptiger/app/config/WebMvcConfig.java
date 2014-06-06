package com.proptiger.app.config;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.VelocityException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.ConversionServiceFactoryBean;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.ui.velocity.VelocityEngineFactory;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.proptiger.data.init.CustomDefaultKeyGenerator;
import com.proptiger.data.init.CustomObjectMapper;
import com.proptiger.data.init.RequestResponseInterceptor;
import com.proptiger.data.util.DateToStringConverter;
import com.proptiger.data.util.LongToDateConverter;
import com.proptiger.data.util.PropertyKeys;
import com.proptiger.data.util.PropertyReader;
import com.proptiger.data.util.StringToDateConverter;

/**
 * Initialize application wide beans here.
 * @author Rajeev Pandey
 *
 */
@Configuration
@ComponentScan(basePackages = { "com.proptiger" })
@EnableWebMvc
@EnableCaching
@EnableAspectJAutoProxy
@PropertySource("classpath:application.properties")
public class WebMvcConfig extends WebMvcConfigurerAdapter {

    @Autowired
    private PropertyReader propertyReader;

    @Bean
    public RequestResponseInterceptor createRequestResponseInterceptor() {
        RequestResponseInterceptor interceptor = new RequestResponseInterceptor();
        interceptor.setRedisHost(propertyReader.getRequiredProperty(PropertyKeys.REDIS_HOST));
        interceptor.setRedisPort(propertyReader.getRequiredPropertyAsType(PropertyKeys.REDIS_PORT, Integer.class));
        return interceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(createRequestResponseInterceptor());
    }

    @Bean(name = "conversionService")
    public ConversionService getConversionService() {
        ConversionServiceFactoryBean bean = new ConversionServiceFactoryBean();
        bean.setConverters(getConverters());
        bean.afterPropertiesSet();
        ConversionService object = bean.getObject();
        return object;
    }

    public Set<Converter<?, ?>> getConverters() {
        Set<Converter<?, ?>> converters = new HashSet<Converter<?, ?>>();
        converters.add(new StringToDateConverter());
        converters.add(new DateToStringConverter());
        converters.add(new LongToDateConverter());
        return converters;
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(messageConverter());
        super.configureMessageConverters(converters);
    }

    @Bean
    public MappingJackson2HttpMessageConverter messageConverter() {
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setObjectMapper(new CustomObjectMapper());
        return converter;
    }

    @Bean(name = "cacheManager")
    public CacheManager cacheManager() {
        RedisCacheManager cacheManager = new RedisCacheManager(getRedisTemplate());
        cacheManager.setDefaultExpiration(propertyReader.getRequiredPropertyAsType(
                PropertyKeys.REDIS_DEFAULT_EXPIRATION_TIME,
                Integer.class));
        cacheManager.setUsePrefix(true);
        return cacheManager;
    }

    @Bean(name = "customKeyGenerator")
    public KeyGenerator keyGenerator() {
        return new CustomDefaultKeyGenerator();
    }

    @Bean(name = "redisTemplate")
    public RedisTemplate<?, ?> getRedisTemplate() {
        RedisTemplate<?, ?> redisTemplate = new RedisTemplate();
        redisTemplate.setConnectionFactory(getJedisConnectionFactory());
        redisTemplate.setValueSerializer(getJDKSerializer());
        return redisTemplate;
    }

    @Bean(name = "jdkSerializer")
    public RedisSerializer<?> getJDKSerializer() {
        return new JdkSerializationRedisSerializer();
    }

    @Bean(name = "jedisConnectionFactory")
    public RedisConnectionFactory getJedisConnectionFactory() {
        JedisConnectionFactory connectionFactory = new JedisConnectionFactory();
        connectionFactory.setHostName(propertyReader.getRequiredProperty(PropertyKeys.REDIS_HOST));
        connectionFactory.setPort(propertyReader.getRequiredPropertyAsType(PropertyKeys.REDIS_PORT, Integer.class));
        connectionFactory.setUsePool(propertyReader.getRequiredPropertyAsType(
                PropertyKeys.REDIS_USE_POOL,
                Boolean.class));
        return connectionFactory;
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
}