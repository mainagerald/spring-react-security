// src/main/java/com/example/demoSecurity/service/impl/UserServiceImpl.java
package com.auth.service.impl;

import com.auth.repository.UserRepository;
import com.auth.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    @Cacheable(key = "#username", value = "UserDetailsCache")
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("Fetching details for user: {}", username);
        return userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found."));
    }
}
