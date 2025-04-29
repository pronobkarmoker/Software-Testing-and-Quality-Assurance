package com.mockito;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.example.auth.model.User;
import com.example.auth.repository.UserRepository;
import com.example.auth.security.HashLibrary;

@RunWith(MockitoJUnitRunner.class)
public class AuthManagerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private HashLibrary hashLibrary;

    private AuthManager authManager;

    private static final String VALID_EMAIL = "user@example.com";
    private static final String VALID_PASSWORD = "password123";
    private static final String INVALID_PASSWORD = "wrongPassword";
    private static final String VALID_HASHED_PASSWORD = "hashedPassword123";
    private static final String NONEXISTENT_EMAIL = "nonexistent@example.com";

    private User validUser;

    @Before
    public void setUp() {
        // Initialize the AuthManager with mocked dependencies
        authManager = new AuthManager(userRepository, hashLibrary);

        // Create a valid user
        validUser = new User();
        validUser.setId("userId123");
        validUser.setEmail(VALID_EMAIL);
        validUser.setHashedPassword(VALID_HASHED_PASSWORD);
        validUser.setFullName("Test User");
        validUser.setActive(true);

        // Configure mock behavior
        when(userRepository.findByEmail(VALID_EMAIL)).thenReturn(validUser);
        when(userRepository.findByEmail(NONEXISTENT_EMAIL)).thenReturn(null);

        when(hashLibrary.verifyPassword(VALID_PASSWORD, VALID_HASHED_PASSWORD)).thenReturn(true);
        when(hashLibrary.verifyPassword(INVALID_PASSWORD, VALID_HASHED_PASSWORD)).thenReturn(false);
    }

    @Test
    public void testLoginByEmailPassword_SuccessfulLogin() {
        // Test successful login
        User authenticatedUser = authManager.loginByEmailPassword(VALID_EMAIL, VALID_PASSWORD);

        // Verify the result
        assertNotNull("Should return a user on successful login", authenticatedUser);
        assertEquals("Should return the correct user", validUser.getId(), authenticatedUser.getId());

        // Verify that the mocks were called with correct parameters
        verify(userRepository).findByEmail(VALID_EMAIL);
        verify(hashLibrary).verifyPassword(VALID_PASSWORD, VALID_HASHED_PASSWORD);
    }

    @Test
    public void testLoginByEmailPassword_WrongPassword() {
        // Test login with wrong password
        User authenticatedUser = authManager.loginByEmailPassword(VALID_EMAIL, INVALID_PASSWORD);

        // Verify the result
        assertNull("Should return null for wrong password", authenticatedUser);

        // Verify that the mocks were called with correct parameters
        verify(userRepository).findByEmail(VALID_EMAIL);
        verify(hashLibrary).verifyPassword(INVALID_PASSWORD, VALID_HASHED_PASSWORD);
    }

    @Test
    public void testLoginByEmailPassword_UserNotFound() {
        // Test login with non-existent email
        User authenticatedUser = authManager.loginByEmailPassword(NONEXISTENT_EMAIL, VALID_PASSWORD);

        // Verify the result
        assertNull("Should return null for non-existent user", authenticatedUser);

        // Verify that the userRepository was called but not hashLibrary
        verify(userRepository).findByEmail(NONEXISTENT_EMAIL);
        verifyNoInteractions(hashLibrary);
    }

    @Test
    public void testLoginByEmailPassword_InactiveUser() {
        // Create an inactive user
        User inactiveUser = new User();
        inactiveUser.setId("inactiveId");
        inactiveUser.setEmail("inactive@example.com");
        inactiveUser.setHashedPassword(VALID_HASHED_PASSWORD);
        inactiveUser.setActive(false);

        // Configure mock to return inactive user
        when(userRepository.findByEmail("inactive@example.com")).thenReturn(inactiveUser);

        // Test login with inactive user
        User authenticatedUser = authManager.loginByEmailPassword("inactive@example.com", VALID_PASSWORD);

        // Verify the result
        assertNull("Should return null for inactive user", authenticatedUser);

        // Verify that userRepository was called but hashLibrary wasn't
        verify(userRepository).findByEmail("inactive@example.com");
        verifyNoInteractions(hashLibrary);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testLoginByEmailPassword_NullEmail() {
        // Test login with null email
        authManager.loginByEmailPassword(null, VALID_PASSWORD);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testLoginByEmailPassword_EmptyEmail() {
        // Test login with empty email
        authManager.loginByEmailPassword("  ", VALID_PASSWORD);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testLoginByEmailPassword_NullPassword() {
        // Test login with null password
        authManager.loginByEmailPassword(VALID_EMAIL, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testLoginByEmailPassword_EmptyPassword() {
        // Test login with empty password
        authManager.loginByEmailPassword(VALID_EMAIL, "  ");
    }
}