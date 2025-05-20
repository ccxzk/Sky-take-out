package com.sky.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;

import javax.annotation.Resource;
import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableCaching
public class CacheConfig {
    @Bean
    public RedisCacheManager redisCacheManager(RedisConnectionFactory factory) {
        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(30)); // 默认缓存时间为30分钟

        // 定义特定缓存的配置
        Map<String, RedisCacheConfiguration> initialCacheConfigurations = new HashMap<>();
        initialCacheConfigurations.put("setmealCache", defaultConfig.entryTtl(Duration.ofSeconds(60))); // setmealCache 的 TTL 设置为 60 秒
        initialCacheConfigurations.put("dishCache", defaultConfig.entryTtl(Duration.ofSeconds(60))); // dishCache 的 TTL 设置为 60 秒

        return RedisCacheManager.builder(factory)
                .cacheDefaults(defaultConfig)
                .withInitialCacheConfigurations(Collections.unmodifiableMap(initialCacheConfigurations))
                .build();
    }
}