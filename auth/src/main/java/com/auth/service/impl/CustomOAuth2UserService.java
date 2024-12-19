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
        if (email == null) {
            throw new OAuth2AuthenticationException("Email not found from OAuth provider");
        }

        User user = userRepository.findByEmail(email)
                .map(existingUser -> updateExistingUser(existingUser, oAuth2User))
                .orElseGet(() -> createNewUser(oAuth2User));

        return oAuth2User;
    }

    private User updateExistingUser(User existingUser, OAuth2User oAuth2User) {
        existingUser.setAttributes(oAuth2User.getAttributes());
        userRepository.save(existingUser);
        return existingUser;
    }

    private User createNewUser(OAuth2User oAuth2User) {
        User user = new User();
        user.setEmail(oAuth2User.getAttribute("email"));
        user.setPublicId(UUID.randomUUID().toString());
        user.setActivated(true);
        user.setAttributes(oAuth2User.getAttributes());

//        todo: CHECK FOR PROVIDER DETAILS
        if ("accounts.google.com".equals(oAuth2User.getAttribute("iss"))) {
            user.setProvider(AuthProvider.GOOGLE);
            user.setProviderId(oAuth2User.getAttribute("sub"));
        } else {
            user.setProvider(AuthProvider.LOCAL);
        }

        return userRepository.save(user);
    }
}
