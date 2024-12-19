//package com.auth.SECURITYTESTS;
//
//import com.auth.SECURITY.model.User;
//import com.auth.SECURITY.repository.UserRepository;
//import com.auth.SECURITY.service.impl.CustomOAuth2UserService;
//import com.auth.SECURITY.enums.AuthProvider ;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.security.oauth2.client.registration.ClientRegistration;
//import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
//import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
//import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
//import org.springframework.security.oauth2.core.user.OAuth2User;
//
//import java.util.HashMap;
//import java.util.Map;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.argThat;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class CustomOAuth2UserServiceTest {
//
//    @Mock
//    private UserRepository userRepository;
//
//    @Mock
//    private OAuth2UserRequest userRequest;
//
//    @Mock
//    private ClientRegistration clientRegistration;
//
//    @Mock
//    private ClientRegistration.ProviderDetails.UserInfoEndpoint userInfoEndpoint; // Mock for UserInfoEndpoint
//
//    @Mock
//    private ClientRegistration.ProviderDetails providerDetails; // Mock for ProviderDetails
//
//    @Mock
//    private OAuth2User oAuth2User;
//
//    @InjectMocks
//    private CustomOAuth2UserService customOAuth2UserService;
//
//    private final String TEST_EMAIL = "test@example.com";
//    private Map<String, Object> attributes;
//
//    @BeforeEach
//    void setUp() {
//        attributes = new HashMap<>();
//        attributes.put("email", TEST_EMAIL);
//        attributes.put("sub", "123456");
//        attributes.put("iss", "accounts.google.com");
//
//        when(oAuth2User.getAttributes()).thenReturn(attributes);
//
//        // Set up the mocks for ClientRegistration
//        when(clientRegistration.getProviderDetails()).thenReturn(providerDetails);
//        when(providerDetails.getUserInfoEndpoint()).thenReturn(userInfoEndpoint);
//        when(userInfoEndpoint.getUri()).thenReturn("https://example.com/userinfo"); // Mock URI
//
//        when(userRequest.getClientRegistration()).thenReturn(clientRegistration);
//    }
//
//    @Test
//    void loadUser_NewGoogleUser_CreatesAndReturnsUser() {
//        // Arrange
//        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.empty());
//        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArguments()[0]);
//
//        // Act
//        OAuth2User result = customOAuth2UserService.loadUser(userRequest);
//
//        // Assert
//        assertNotNull(result);
//        assertEquals(TEST_EMAIL, result.getAttribute("email"));
//        verify(userRepository).save(argThat(user ->
//                user.getEmail().equals(TEST_EMAIL) &&
//                        user.getProvider() == AuthProvider.GOOGLE &&
//                        user.isActivated()
//        ));
//    }
//
//    @Test
//    void loadUser_ExistingUser_UpdatesAndReturnsUser() {
//        // Arrange
//        User existingUser = new User();
//        existingUser.setEmail(TEST_EMAIL);
//        existingUser.setProvider(AuthProvider.GOOGLE);
//
//        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(existingUser));
//        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArguments()[0]);
//
//        // Act
//        OAuth2User result = customOAuth2UserService.loadUser(userRequest);
//
//        // Assert
//        assertNotNull(result);
//        assertEquals(TEST_EMAIL, result.getAttribute("email"));
//        verify(userRepository).save(argThat(user ->
//                user.getEmail().equals(TEST_EMAIL) &&
//                        user.getAttributes() == attributes
//        ));
//    }
//
//    @Test
//    void loadUser_NoEmail_ThrowsException() {
//        // Arrange
//        attributes.remove("email");
//        when(oAuth2User.getAttribute("email")).thenReturn(null);
//
//        // Act & Assert
//        assertThrows(OAuth2AuthenticationException.class, () ->
//                customOAuth2UserService.loadUser(userRequest)
//        );
//        verify(userRepository, never()).save(any());
//    }
//}