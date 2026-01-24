package com.example.catalog.controller;

import com.example.catalog.model.Product;
import com.github.benmanes.caffeine.cache.Cache;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/internal/cache")
public class CacheStatsController {
    private final Cache<Long, Product> productCache;

    public CacheStatsController(Cache<Long, Product> productCache){
        this.productCache = productCache;
    }

    @GetMapping("/stats")
    public Map<String, Object> stats(){
        var stats = productCache.stats();

        return Map.of("hitRate", stats.hitRate(),
                "hitCount", stats.hitCount(),
                "missCount", stats.missCount(),
                "evictionCount", stats.evictionCount());
    }
}
