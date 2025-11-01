package com.expense.splitter.service;

import com.expense.splitter.dto.AuthResponse;
import com.expense.splitter.dto.LoginRequest;
import com.expense.splitter.model.User;
import com.expense.splitter.repository.UserRepository;
import com.expense.splitter.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Service for authentication operations.
 * Handles user login, registration, and JWT token generation.
 */
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    // ✅ Explicit constructor injection (no Lombok)
    @Autowired
    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    /**
     * Authenticate the user and return JWT token.
     */
    public AuthResponse login(LoginRequest request) {
        // ✅ Find user by username
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Invalid username or password"));

        // ✅ Check password
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid username or password");
        }

        // ✅ Generate JWT token
        String token = jwtTokenProvider.generateToken(user.getUsername());

        // ✅ Return AuthResponse DTO
        return new AuthResponse(token, user.getUsername());
    }

    /**
     * Register a new user account.
     */
    public void register(String username, String password) {
        // ✅ Check if username already exists
        if (userRepository.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("Username already exists");
        }

        // ✅ Create and save new user
        User newUser = new User();
        newUser.setUsername(username);
        newUser.setPassword(passwordEncoder.encode(password));
        userRepository.save(newUser);
    }
}
