package com.example.catalog.config;

import com.example.catalog.model.Product;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class InProcessCacheConfig {
    @Bean
    public Cache<Long, Product> productCache(
            @Value("${cache.inprocess.ttlSeconds}") long ttlSeconds,
            @Value("${cache.inprocess.maxSize}") long maxSize
    )
    {
        return Caffeine.newBuilder()
                .expireAfterWrite(Duration.ofSeconds(ttlSeconds))
                .maximumSize(maxSize)
                .recordStats()
                .build();
    }
}
