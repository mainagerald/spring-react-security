package com.auth.config;

import com.hazelcast.config.Config;
import com.hazelcast.config.MapConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HazelcastConfig {
    @Bean
    public HazelcastInstance hazelcastInstance() {
        Config config = new Config();

        // User Details Cache Configuration
        MapConfig userDetailsCache = new MapConfig()
                .setName("userDetailsCache")
                .setTimeToLiveSeconds(3600) // 1 hour cache expiration
                .setMaxIdleSeconds(1800);   // 30 minutes idle time
        config.getMapConfigs().put("userDetailsCache", userDetailsCache);

        // Token Blacklist Cache Configuration
        MapConfig tokenBlacklistCache = new MapConfig()
                .setName("tokenBlacklistCache")
                .setTimeToLiveSeconds(86400); // 24 hours
        config.getMapConfigs().put("tokenBlacklistCache", tokenBlacklistCache);

        return Hazelcast.newHazelcastInstance(config);
    }
}
