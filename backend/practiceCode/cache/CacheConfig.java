//package com.roomate.app.cache;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import java.util.concurrent.TimeUnit;
//
//@Configuration
//public class CacheConfig {
//
//    @Bean(name = "Login attempt cache")
//    // Email = String, Integer = login Attempts
//    public CacheStore<String, Integer> cacheStore() {
//        return new CacheStore<>(10, TimeUnit.MINUTES);
//    }
//
//    // FUTURE REFERENCE IF NEEDED
////    @Bean(name = "Registration  cache")
////    public CacheStore<String, Integer> cacheStore() {
////        return new CacheStore<>(800, TimeUnit.MINUTES);
////    }
//}
