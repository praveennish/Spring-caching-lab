package com.example.catalog.cache;

import java.time.Duration;
import java.util.Optional;

public interface RemoteCache<K, V> {
    Optional<V> get(K key);
    void put(K key, V value, Duration ttl);
    void evict(K key);
}
