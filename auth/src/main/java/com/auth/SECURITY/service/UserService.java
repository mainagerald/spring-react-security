package com.auth.SECURITY.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService{
    UserDetails loadUserByUsername(String username);
    void checkCache(String username);
}
