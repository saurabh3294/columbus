package com.proptiger.app.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.CustomRedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;

import com.proptiger.data.init.CustomDefaultKeyGenerator;
import com.proptiger.data.util.PropertyKeys;
import com.proptiger.data.util.PropertyReader;

/**
 * Configurations for caching related beans
 * @author Rajeev Pandey
 *
 */
@Configuration
@EnableCaching
public class AppCachingConfig implements CachingConfigurer {

    @Autowired
    private PropertyReader propertyReader;

    @Bean(name = "cacheManager")
    @Override
    public CacheManager cacheManager() {
        CustomRedisCacheManager cacheManager = new CustomRedisCacheManager(getRedisTemplate());
        cacheManager.setDefaultExpiration(propertyReader.getRequiredPropertyAsType(
                PropertyKeys.REDIS_DEFAULT_EXPIRATION_TIME,
                Integer.class));
        cacheManager.setUsePrefix(true);
        return cacheManager;
    }

    @Bean(name = "redisTemplate")
    public RedisTemplate<?, ?> getRedisTemplate() {
        RedisTemplate<?, ?> redisTemplate = new RedisTemplate();
        redisTemplate.setConnectionFactory(getJedisConnectionFactory());
        redisTemplate.setValueSerializer(getJDKSerializer());
        return redisTemplate;
    }

    @Bean(name = "customKeyGenerator")
    @Override
    public KeyGenerator keyGenerator() {
        return new CustomDefaultKeyGenerator();
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
}
