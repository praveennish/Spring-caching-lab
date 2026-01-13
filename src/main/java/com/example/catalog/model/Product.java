package com.example.catalog.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "products")
@Getter
public class Product {
    @Id
    private Long id;

    private String name;

    private BigDecimal price;

    private Integer inventory;

    @Column(name = "updated_at")
    private Instant updatedAt;
}
