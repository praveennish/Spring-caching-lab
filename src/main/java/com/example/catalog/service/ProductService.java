package com.example.catalog.service;

import com.example.catalog.cache.RemoteCache;
import com.example.catalog.model.Product;
import com.example.catalog.repository.ProductRepository;
import com.github.benmanes.caffeine.cache.Cache;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ProductService {

    private final ProductRepository repository;
    private final Cache<Long, Product> localCache;
    private final Optional<RemoteCache<Long, Product>> remoteCache;
    private final boolean localCacheEnabled;
    private final Duration remoteTtl;

    private final ConcurrentHashMap<Long, Object> keyLocks = new ConcurrentHashMap<>();

    public ProductService(ProductRepository repository,
                          Cache<Long, Product> localCache,
                          Optional<RemoteCache<Long, Product>> remoteCache,
                          @Value("${cache.remote.ttlSeconds}") long remoteTtlSeconds,
                          @Value("${cache.inprocess.enabled}") boolean localCacheEnabled) {

        this.repository = repository;
        this.localCache = localCache;
        this.remoteCache = remoteCache;
        this.localCacheEnabled = localCacheEnabled;
        this.remoteTtl = Duration.ofSeconds(remoteTtlSeconds);
    }

    public Product getProduct(Long id) {

        // -------- L1 Cache --------
        if (localCacheEnabled) {
            Product local = localCache.getIfPresent(id);
            if (local != null) {
                return local;
            }
        }

        // -------- L2 Cache --------
        if (remoteCache.isPresent()) {
            Optional<Product> remote = remoteCache.get().get(id);
            if (remote.isPresent()) {
                populateLocalCache(id, remote.get());
                return remote.get();
            }
        }

        // -------- SingleFlight DB Load --------
        return loadThroughSingleFlight(id);
    }

    private Product loadThroughSingleFlight(Long id) {

        Object lock = keyLocks.computeIfAbsent(id, k -> new Object());

        synchronized (lock) {
            try {
                // Double-check remote cache after acquiring lock
                if (remoteCache.isPresent()) {
                    Optional<Product> cached = remoteCache.get().get(id);
                    if (cached.isPresent()) {
                        populateLocalCache(id, cached.get());
                        return cached.get();
                    }
                }

                // DB load (only one thread per key)
                Product db = repository.findByIdExplicit(id)
                        .orElseThrow(() -> new RuntimeException("Product not found"));

                // Populate caches exactly once
                populateRemoteCache(id, db);
                populateLocalCache(id, db);

                return db;

            } finally {
                // Remove only if same lock instance (safety)
                keyLocks.remove(id, lock);
            }
        }
    }

    private void populateLocalCache(Long id, Product product) {
        if (localCacheEnabled) {
            localCache.put(id, product);
        }
    }

    private void populateRemoteCache(Long id, Product product) {
        remoteCache.ifPresent(cache ->
                cache.put(id, product, remoteTtl)
        );
    }
}
