//package com.roomate.app.cache;
//
//import com.google.common.cache.Cache;
//import com.google.common.cache.CacheBuilder;
//import jakarta.validation.constraints.NotNull;
//import lombok.extern.slf4j.Slf4j;
//
//import java.util.concurrent.TimeUnit;
//
//@Slf4j
////EFFECT: Defines what cache is meant to be
//public class CacheStore<K,V> {
//    private final Cache<K,V> cache;
//
//    public CacheStore(int expireDuration, TimeUnit timeUnit) {
//        cache = CacheBuilder.newBuilder()
//                .expireAfterWrite(expireDuration, timeUnit)
//                .concurrencyLevel(Runtime.getRuntime().availableProcessors())
//                .build();
//    }
//
//    public V get(@NotNull K key) {
//        log.info("CacheStore.get() key: {}", key.toString());
//        return cache.getIfPresent(key);
//    }
//
//    public void put(@NotNull K key, @NotNull V value) {
//        log.info("Put into cache key: {}, value: {}", key.toString(), value.toString());
//        cache.put(key, value);
//    }
//
//    public void invalidate(@NotNull K key) {
//        log.info("Evict from cache key: {}", key.toString());
//        cache.invalidate(key);
//    }
//}
