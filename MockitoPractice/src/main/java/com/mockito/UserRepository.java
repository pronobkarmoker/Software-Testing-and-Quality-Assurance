package com.mockito;

public interface UserRepository {
    User findByEmail(String email);
}
