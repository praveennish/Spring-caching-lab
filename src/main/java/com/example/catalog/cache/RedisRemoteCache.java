package com.example.catalog.cache;

import com.example.catalog.model.Product;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Optional;

@Component
@ConditionalOnProperty(name = "cache.remote.backend", havingValue = "redis")
public class RedisRemoteCache implements RemoteCache<Long, Product> {

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper mapper;

    public RedisRemoteCache(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.mapper = JsonMapper.builder()
                .addModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .build();
    }

    @Override
    public Optional<Product> get(Long key) {
        try {
            String json = redisTemplate.opsForValue().get(key.toString());
            if (json == null) return Optional.empty();
            return Optional.of(mapper.readValue(json, Product.class));
        } catch (Exception e) {
            throw new RuntimeException("Redis deserialization failed", e);
        }
    }

    @Override
    public void put(Long key, Product value, Duration ttl) {
        try {
            String json = mapper.writeValueAsString(value);
            redisTemplate.opsForValue().set(key.toString(), json, ttl);
        } catch (Exception e) {
            throw new RuntimeException("Redis serialization failed", e);
        }
    }
}
