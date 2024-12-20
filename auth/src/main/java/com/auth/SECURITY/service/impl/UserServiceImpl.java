// src/main/java/com/example/demoSecurity/service/impl/UserServiceImpl.java
package com.auth.SECURITY.service.impl;

import com.auth.SECURITY.model.User;
import com.auth.SECURITY.repository.UserRepository;
import com.auth.SECURITY.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final CacheManager cacheManager;

    @Override
    @Cacheable(value = "UserDetailsCache", key = "#username")  // Make sure this matches your Hazelcast config
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("Fetching details for user: {}", username);
        return userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found."));
    }
    public User getAuthenticatedUser(){
        Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
        if(authentication==null||!authentication.isAuthenticated()){
            throw new RuntimeException("No authenticated user found");
        }
        Object principal = authentication.getPrincipal();
        if(principal instanceof User){
            return (User) principal;
        }else {
            throw new RuntimeException("Authentication principal not of type user");
        }
    }
//    test cache brute
    public void checkCache(String username) {
        var cache = cacheManager.getCache("UserDetailsCache");
        if (cache != null) {
            var cacheEntry = cache.get(username);
            log.info("Cache entry for {}: {}", username, cacheEntry);
        }
    }

}