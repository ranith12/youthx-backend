package com.youthx.backend.service;

import com.youthx.backend.entity.User;
import com.youthx.backend.exception.DuplicateResourceException;
import com.youthx.backend.exception.InvalidCredentialsException;
import com.youthx.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User getById(UUID currentUserId) {
        return userRepository.findById(currentUserId)
                .orElseThrow(() ->
                        new NoSuchElementException(
                                "User not found with id: " + currentUserId
                        ));
    }

    public User register(String email, String password, String fullName) {
        if (userRepository.existsByEmail(email)) {
            throw new DuplicateResourceException(
                    "Email is already registered: " + email
            );
        }

        User user = new User();
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(password));
        user.setFullName(fullName);
        user.setCreatedAt(OffsetDateTime.now());

        return userRepository.save(user);
    }

    public User login(String email, String password) {
        User user = userRepository.findByEmail(email).orElse(null);

        if (user == null || !passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new InvalidCredentialsException("Invalid credentials");
        }

        return user;
    }
}