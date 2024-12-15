package com.auth.service.impl;


import com.auth.enums.AuthProvider;
import com.auth.enums.Role;
import com.auth.model.User;
import com.auth.repository.UserRepository;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.hibernate.id.uuid.UuidGenerator;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    private final UserRepository userRepository;

    public CustomOAuth2UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String email = oAuth2User.getAttribute("email");
        User user = userRepository.findByEmail(email)
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setEmail(oAuth2User.getAttribute("email"));
                    newUser.setPublicId(UUID.randomUUID().toString());
                    newUser.setAttributes(oAuth2User.getAttributes());
                    newUser.setRole(Role.ROLE_TENANT);

                    // Checking for Google OAuth
                    if ("accounts.google.com".equals(oAuth2User.getAttribute("iss"))) {
                        newUser.setProvider(AuthProvider.GOOGLE);
                        newUser.setProviderId(oAuth2User.getAttribute("sub"));
//                    }
//                    // Checking for GitHub OAuth
//                    else if (oAuth2User.getAttribute("login") != null) {
//                        newUser.setProvider(AuthProvider.GITHUB);
//                        newUser.setProviderId(oAuth2User.getAttribute("id").toString());
                    } else {
                        newUser.setProvider(AuthProvider.LOCAL);
                    }
                    return userRepository.save(newUser);
                });

        // Set attributes for the existing user
        user.setAttributes(oAuth2User.getAttributes());

        return user;
    }
}
