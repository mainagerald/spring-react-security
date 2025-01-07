package com.auth.SECURITY.service.impl;

import com.auth.SECURITY.dto.ReadUserDTO;
import com.auth.SECURITY.exceptions.AuthenticationException;
import com.auth.SECURITY.exceptions.NotFoundException;
import com.auth.SECURITY.exceptions.ValidationException;
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
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

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
    @Override
    public ReadUserDTO getByPublicId(String publicId){
        User user = userRepository.findByPublicId(publicId);
        if(user!=null){
            return mapUserToReadUserDto(user);
        }else {
            throw new RuntimeException("User not found");
        }
    }
    @Override
    public ReadUserDTO getAuthenticatedUser() {
        Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
        if(authentication==null|| !authentication.isAuthenticated()){
            throw new RuntimeException("No authentication found");
        }
        Object principal = authentication.getPrincipal();
        try {
            if (principal instanceof User user) {
                validateUserFields(user);
                return mapUserToReadUserDto(user);
            } else if (principal instanceof OAuth2User oAuth2User) {
                validateOAuth2UserFields(oAuth2User);
                return mapOauth2UserToReadUserDto(oAuth2User);
            } else {
                throw new AuthenticationException("Unsupported authentication type: " + principal.getClass().getName());
            }
        } catch (Exception e) {
            log.error("Error processing authenticated user", e);
            throw new AuthenticationException("Failed to process authenticated user: " + e.getMessage());
        }
    }

    private ReadUserDTO mapUserToReadUserDto(User user){
        ReadUserDTO userDto = new ReadUserDTO();
        userDto.setRole(user.getRole());
        userDto.setEmail(user.getEmail());
        userDto.setPublicId(UUID.fromString(user.getPublicId()));
        return userDto;
    }
    private ReadUserDTO mapOauth2UserToReadUserDto(OAuth2User oAuth2User){
        ReadUserDTO userDTO= new ReadUserDTO();
        userDTO.setEmail(oAuth2User.getAttribute("email"));
        User user = userRepository.findByEmail(oAuth2User.getAttribute("email"))
                .orElseThrow(()->new NotFoundException("User with given email not found"));
        userDTO.setPublicId(UUID.fromString(user.getPublicId()));
        userDTO.setRole(user.getRole());
        return userDTO;
    }

    private void validateUserFields(User user) {
        if (user.getEmail() == null || user.getEmail().isEmpty()) {
            throw new ValidationException("User email is required");
        }
        if (user.getPublicId() == null) {
            throw new ValidationException("User public ID is required");
        }
        if (user.getRole() == null) {
            throw new ValidationException("User role is required");
        }
    }
    private void validateOAuth2UserFields(OAuth2User oAuth2User) {
        Map<String, Object> attributes = oAuth2User.getAttributes();
        if (attributes == null) {
            throw new ValidationException("OAuth2User attributes are missing");
        }
        if (!attributes.containsKey("email") || attributes.get("email") == null) {
            throw new ValidationException("OAuth2User email is required");
        }
        if (!attributes.containsKey("sub") || attributes.get("sub") == null) {
            throw new ValidationException("OAuth2User sub is required");
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