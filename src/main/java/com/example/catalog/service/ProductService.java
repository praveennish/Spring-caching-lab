package com.example.catalog.service;

import com.example.catalog.cache.RemoteCache;
import com.example.catalog.dto.UpdateProductRequest;
import com.example.catalog.model.Product;
import com.example.catalog.repository.ProductRepository;
import com.github.benmanes.caffeine.cache.Cache;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ProductService {

    private final ProductRepository repository;
    private final ProductCacheService cacheService;
    private final CacheVersionService versionService;

    public ProductService(
            ProductRepository repository,
            ProductCacheService cacheService,
            CacheVersionService versionService
    ) {
        this.repository = repository;
        this.cacheService = cacheService;
        this.versionService = versionService;
    }

    public Product getProduct(Long id) {
        Product cached = cacheService.get(id);
        if (cached != null) {
            return cached;
        }

        Product product = repository.findById(id)
                .orElseThrow();

        cacheService.put(product);
        return product;
    }

    @Transactional
    public Product updatePrice(Long id, BigDecimal price) {

        Product product = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found: " + id));

        product.setPrice(price);
        repository.save(product);

        // 🔥 Replace cache delete with version bump
        versionService.bumpVersion("product");

        return product;
    }


    @Transactional
    public Product updateProduct(Long id, UpdateProductRequest request) {
        Product product = repository.findById(id)
                .orElseThrow();

        product.setPrice(BigDecimal.valueOf(request.getPrice()));
        product.setName(request.getName());

        repository.save(product);

        // 🔥 KEY IDEA
        // Instead of deleting cache → bump version
        versionService.bumpVersion("product");

        return product;
    }
}
