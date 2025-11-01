package com.expense.splitter.controller;

import com.expense.splitter.dto.AuthResponse;
import com.expense.splitter.dto.LoginRequest;
import com.expense.splitter.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for Authentication
 * Handles user login and registration endpoints
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:5173")
public class AuthController {

    private final AuthService authService;

    // ✅ Manual constructor injection (fixes "blank final field" error)
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Login endpoint
     * POST /api/auth/login
     * Returns JWT token for authenticated requests
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Register endpoint
     * POST /api/auth/register
     * Creates a new user account
     */
    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody LoginRequest request) {
        authService.register(request.getUsername(), request.getPassword());
        return ResponseEntity.ok("User registered successfully");
    }
}
