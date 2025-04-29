package com.mockito;

public class AuthManager {
    private final UserRepository userRepository;
    private final HashLibrary hashLibrary;

    public AuthManager(UserRepository userRepository, HashLibrary hashLibrary) {
        this.userRepository = userRepository;
        this.hashLibrary = hashLibrary;
    }


    public User loginByEmailPassword(String email, String password) {
        // Validate inputs
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }

        // Find user by email
        User user = userRepository.findByEmail(email);
        if (user == null) {
            return null; // User not found
        }

        // Check if user is active
        if (!user.isActive()) {
            return null; // Inactive user
        }

        // Verify password
        if (hashLibrary.verifyPassword(password, user.getHashedPassword())) {
            return user; // Authentication successful
        } else {
            return null; // Wrong password
        }
    }
}
