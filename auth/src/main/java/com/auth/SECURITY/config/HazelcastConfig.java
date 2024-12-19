package com.auth.SECURITY.config;

import com.hazelcast.config.Config;
import com.hazelcast.config.MapConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.spring.cache.HazelcastCacheManager;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class HazelcastConfig {
    @Bean
    public HazelcastInstance hazelcastInstance() {
        Config config = new Config();

        MapConfig userDetailsCache = new MapConfig()
                .setName("UserDetailsCache")
                .setTimeToLiveSeconds(3600)
                .setMaxIdleSeconds(1800);

        config.getMapConfigs().put("UserDetailsCache", userDetailsCache);

        MapConfig tokenBlacklistCache = new MapConfig()
                .setName("tokenBlacklistCache")
                .setTimeToLiveSeconds(86400);

        config.getMapConfigs().put("tokenBlacklistCache", tokenBlacklistCache);

        return Hazelcast.newHazelcastInstance(config);
    }

    @Bean
    public CacheManager cacheManager(HazelcastInstance hazelcastInstance) {
        return new HazelcastCacheManager(hazelcastInstance);
    }
}