package com.example.catalog.service;

import com.example.catalog.cache.RemoteCache;
import com.example.catalog.model.Product;
import com.example.catalog.repository.ProductRepository;
import com.github.benmanes.caffeine.cache.Cache;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;

@Service
public class ProductService {
    private final ProductRepository repository;
    private final Cache<Long, Product> productCache;
    private final boolean cacheEnabled;
    private final boolean remoteEnabled;
    private final Optional<RemoteCache<Long, Product>> remoteCache;
    private final Duration remoteTtl;


    public ProductService(ProductRepository repository,
                          Cache<Long, Product> productCache,
                          Optional<RemoteCache<Long, Product>> remoteCache,
                          @Value("${cache.remote.enabled}") boolean remoteEnabled,
                          @Value("${cache.remote.ttlSeconds}") long remoteTtl,
                          @Value("${cache.inprocess.enabled}") boolean cacheEnabled){
        this.repository = repository;
        this.productCache = productCache;
        this.cacheEnabled = cacheEnabled;
        this.remoteCache = remoteCache;
        this.remoteEnabled = remoteEnabled;
        this.remoteTtl = Duration.ofSeconds(remoteTtl);
    }

    public Product getProduct(Long id){
       if (cacheEnabled){
           Product local = productCache.getIfPresent(id);
           if (local != null)
               return local;
       }

       if (remoteEnabled && remoteCache.isPresent()){
           Optional<Product> remote = remoteCache.get().get(id);

           if(remote.isPresent()){
               productCache.put(id, remote.get());
               return remote.get();
           }
       }

       Product db = loadFromDb(id);

        if (remoteEnabled && remoteCache.isPresent()) {
            remoteCache.get().put(id, db, remoteTtl);
        }

        if (cacheEnabled) {
            productCache.put(id, db);
        }

        return db;
    }

    private Product loadFromDb(Long id){
        return repository.findByIdExplicit(id)
                .orElseThrow(() -> new RuntimeException("Product not found!"));
    }
}
