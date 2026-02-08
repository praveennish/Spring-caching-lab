package com.example.catalog.service;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class CacheVersionService {

    private final StringRedisTemplate redis;

    public CacheVersionService(StringRedisTemplate redis) {
        this.redis = redis;
    }

    public long getVersion(String entity) {
        String key = entity + ":version";
        String val = redis.opsForValue().get(key);
        if (val == null) {
            redis.opsForValue().set(key, "1");
            return 1;
        }
        return Long.parseLong(val);
    }

    public long bumpVersion(String entity) {
        return redis.opsForValue().increment(entity + ":version");
    }
}

