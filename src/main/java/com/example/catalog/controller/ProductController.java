package com.example.catalog.controller;

import com.example.catalog.model.Product;
import com.example.catalog.service.ProductService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {
    private final ProductService service;

    public ProductController(ProductService service){
        this.service = service;
    }

    @GetMapping("/{id}")
    public Product getProduct(@PathVariable Long id){
        return service.getProduct(id);
    }
}
