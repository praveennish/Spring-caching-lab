package com.example.catalog.service;

import com.example.catalog.model.Product;
import com.example.catalog.repository.ProductRepository;
import org.springframework.stereotype.Service;

@Service
public class ProductService {
    private final ProductRepository repository;

    public ProductService(ProductRepository repository){
        this.repository = repository;
    }

    public Product getProduct(Long id){
        return repository.findByIdExplicit(id)
                .orElseThrow(() -> new RuntimeException("Product not found!"));
    }
}
