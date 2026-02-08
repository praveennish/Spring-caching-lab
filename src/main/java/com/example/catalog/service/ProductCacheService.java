package com.example.catalog.service;

import com.example.catalog.cache.VersionedCacheKeyBuilder;
import com.example.catalog.model.Product;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class ProductCacheService {

    private final RedisTemplate<String, Product> redisTemplate;
    private final VersionedCacheKeyBuilder keyBuilder;

    private static final Duration TTL = Duration.ofMinutes(5);

    public ProductCacheService(
            RedisTemplate<String, Product> redisTemplate,
            VersionedCacheKeyBuilder keyBuilder
    ) {
        this.redisTemplate = redisTemplate;
        this.keyBuilder = keyBuilder;
    }

    public Product get(Long productId) {
        String key = keyBuilder.productKey(productId);
        return redisTemplate.opsForValue().get(key);
    }

    public void put(Product product) {
        String key = keyBuilder.productKey(product.getId());
        redisTemplate.opsForValue().set(key, product, TTL);
    }
}
