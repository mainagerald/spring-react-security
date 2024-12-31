//package com.auth.SECURITY.config;
//
//import com.github.benmanes.caffeine.cache.Caffeine;
//import org.springframework.cache.CacheManager;
//import org.springframework.cache.annotation.EnableCaching;
//import org.springframework.cache.caffeine.CaffeineCacheManager;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import java.util.concurrent.TimeUnit;
//
//@Configuration
//@EnableCaching
//public class CacheConfig {
//  TODO: OPT FOR THIS CAFFEINE SETUP IN CASE OF COMPLEX SETUP WITH HAZELCAST

//    @Bean
//    public Caffeine<Object, Object> caffeineConfig() {
//        return Caffeine.newBuilder()
//                .expireAfterWrite(1, TimeUnit.HOURS)
//                .initialCapacity(100)
//                .maximumSize(500);
//    }
//
//    @Bean
//    public CacheManager cacheManager(Caffeine<Object, Object> caffeine) {
//        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
//        cacheManager.setCaffeine(caffeine);
//        cacheManager.registerCustomCache("UserDetailsCache",
//                Caffeine.newBuilder()
//                        .expireAfterWrite(1, TimeUnit.HOURS)
//                        .maximumSize(1000)
//                        .build());
//        cacheManager.registerCustomCache("tokenBlacklistCache",
//                Caffeine.newBuilder()
//                        .expireAfterWrite(24, TimeUnit.HOURS)
//                        .maximumSize(10000)
//                        .build());
//        return cacheManager;
//    }
//}