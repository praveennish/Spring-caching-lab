package com.example.catalog.cache;

import com.example.catalog.service.CacheVersionService;
import org.springframework.stereotype.Component;

@Component
public class VersionedCacheKeyBuilder {

    private final CacheVersionService versionService;

    public VersionedCacheKeyBuilder(CacheVersionService versionService) {
        this.versionService = versionService;
    }

    public String productKey(Long productId) {
        long version = versionService.getVersion("product");
        return "product:v" + version + ":" + productId;
    }
}
