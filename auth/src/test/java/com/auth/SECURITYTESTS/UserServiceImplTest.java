package com.auth.SECURITYTESTS;

import com.auth.SECURITY.enums.Role;
import com.auth.SECURITY.model.User;
import com.auth.SECURITY.repository.UserRepository;
import com.auth.SECURITY.service.impl.UserServiceImpl;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.spring.cache.HazelcastCacheManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    private HazelcastCacheManager cacheManager;
    private UserServiceImpl userService;
    private User testUser;
    private final String TEST_EMAIL = "test@example.com";
    private HazelcastInstance hazelcastInstance;

    @BeforeEach
    void setUp() {
        // Initialize mocks
        MockitoAnnotations.openMocks(this);

        // Setup real Hazelcast cache
        hazelcastInstance = Hazelcast.newHazelcastInstance();
        cacheManager = new HazelcastCacheManager(hazelcastInstance);

        // Initialize service with real cache manager and mock repository
        userService = new UserServiceImpl(userRepository, cacheManager);

        // Setup test user
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail(TEST_EMAIL);
        testUser.setPublicId(UUID.randomUUID().toString());
        testUser.setPassword("encoded_password");
        testUser.setRole(Role.ROLE_TENANT);
        testUser.setActivated(true);

        // Initialize Hazelcast for integration testing if needed
        hazelcastInstance = Hazelcast.newHazelcastInstance();
    }

    @AfterEach
    void tearDown() {
        if (hazelcastInstance != null) {
            hazelcastInstance.shutdown();
        }
    }

    @Test
    void loadUserByUsername_UserExists_ReturnsUserDetails() {
        // Arrange
        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(testUser));

        // Act
        UserDetails result = userService.loadUserByUsername(TEST_EMAIL);

        // Assert
        assertNotNull(result);
        assertEquals(TEST_EMAIL, result.getUsername());
        verify(userRepository, times(1)).findByEmail(TEST_EMAIL);
    }

    @Test
    void loadUserByUsername_UserNotFound_ThrowsException() {
        // Arrange
        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UsernameNotFoundException.class, () ->
                userService.loadUserByUsername(TEST_EMAIL)
        );
        verify(userRepository, times(1)).findByEmail(TEST_EMAIL);
    }

//    @Test
//    void loadUserByUsername_VerifyCaching() {
//        // Arrange
//        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(testUser));
//
//        // Act
//        UserDetails firstCall = userService.loadUserByUsername(TEST_EMAIL);
//        UserDetails secondCall = userService.loadUserByUsername(TEST_EMAIL);
//
//        // Assert
//        verify(userRepository, times(1)).findByEmail(TEST_EMAIL);
//        assertNotNull(firstCall);
//        assertNotNull(secondCall);
//        assertEquals(firstCall, secondCall);
//    }
}