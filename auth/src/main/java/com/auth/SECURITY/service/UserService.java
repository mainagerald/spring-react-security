package com.auth.SECURITY.service;

import com.auth.SECURITY.dto.ReadUserDTO;
import com.auth.SECURITY.model.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService{
    UserDetails loadUserByUsername(String username);
    void checkCache(String username);
    ReadUserDTO getAuthenticatedUser();
    ReadUserDTO getByPublicId(String publicId);}
