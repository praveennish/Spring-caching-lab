package com.example.catalog.cache;

import com.example.catalog.model.Product;
import net.spy.memcached.MemcachedClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.util.Optional;

@Component
@ConditionalOnProperty(name = "cache.remote.backend", havingValue = "memcached")
public class MemcachedRemoteCache implements RemoteCache<Long, Product> {

    private final MemcachedClient client;

    public MemcachedRemoteCache() throws IOException {
        this.client = new MemcachedClient(
                new InetSocketAddress("localhost", 11211));
    }

    @Override
    public Optional<Product> get(Long key) {
        return Optional.ofNullable((Product) client.get(key.toString()));
    }

    @Override
    public void put(Long key, Product value, Duration ttl) {
        client.set(key.toString(), (int) ttl.getSeconds(), value);
    }
}

