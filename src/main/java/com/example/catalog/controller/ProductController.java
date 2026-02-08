package com.example.catalog.controller;

import com.example.catalog.dto.UpdateProductRequest;
import com.example.catalog.model.Product;
import com.example.catalog.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

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

    @PutMapping("/{id}")
    public ResponseEntity<Product> updatePrice(
            @PathVariable Long id,
            @RequestParam BigDecimal price) {

        Product updated = service.(id, price);
        return ResponseEntity.ok(updated);
    }

    @PutMapping("/products/{id}")
    public Product update(
            @PathVariable Long id,
            @RequestBody UpdateProductRequest request
    ) {
        return service.updateProduct(id, request);
    }

}
