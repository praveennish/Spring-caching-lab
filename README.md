# Spring-caching-lab
Spring based caching lab 

# Architecture diagram

[ Client (k6 / Browser) ]
          |
          |  HTTP GET /api/v1/products/{id}
          |  Cache-Control / ETag
          v
[ Nginx Reverse Proxy ]
  - HTTP cache
  - Cache key: URI
  - TTL configurable
          |
          v
[ Spring Boot Application ]
  |
  |-- Controller
  |
  |-- Service Layer
  |     |
  |     |-- In-Process Cache (Caffeine)
  |     |
  |     |-- Remote Cache Interface
  |           |-- Redis Implementation
  |           |-- Memcached Implementation
  |
  |-- Repository Layer
          |
          v
[ PostgreSQL ]
  - Shared buffers
  - Query plan cache
          |
          v
[ OS Page Cache ]

