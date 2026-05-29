package com.smartrecrute.smartrecrute.controller;

import com.smartrecrute.smartrecrute.dto.*;
import com.smartrecrute.smartrecrute.service.AuthService;
import com.smartrecrute.smartrecrute.entity.Utilisateur;
import com.smartrecrute.smartrecrute.jwt.Authentification;
import com.smartrecrute.smartrecrute.jwt.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private JwtService jwtService;

    @PostMapping("/register")
    public ResponseEntity<Utilisateur> register(@RequestBody UserRegistrationRequest request) {
        Utilisateur user = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody Authentification authentification) {
        try {
            return ResponseEntity.ok(jwtService.generate(authentification.username()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(401).body(Map.of("message", "Login failed: " + e.getMessage()));
        }
    }

    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(@RequestHeader("Authorization") String authHeader, @RequestBody ChangePasswordRequest request) {
        try {
            // Extract email from JWT token to ensure we get the authenticated user
            String token = authHeader.substring(7);
            String email = jwtService.extractUsername(token);
            authService.changePassword(email, request);
            return ResponseEntity.ok("Password changed successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    // Alternative: change password without old password check (for when user is already authenticated)
    @PostMapping("/set-password")
    public ResponseEntity<String> setPassword(@RequestHeader("Authorization") String authHeader, @RequestBody Map<String, Object> request) {
        try {
            String newPassword = (String) request.get("newPassword");
            if (newPassword == null || newPassword.isEmpty()) {
                return ResponseEntity.badRequest().body("New password required");
            }
            // Extract email from JWT token
            String token = authHeader.substring(7);
            String email = jwtService.extractUsername(token);
            authService.setPasswordDirect(email, newPassword);
            return ResponseEntity.ok("Password set successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, String>> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        try {
            authService.forgotPassword(request);
            return ResponseEntity.ok(Map.of("message", "Password reset email sent"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, String>> resetPassword(@RequestBody ResetPasswordRequest request) {
        try {
            authService.resetPassword(request);
            return ResponseEntity.ok(Map.of("message", "Password reset successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> getCurrentUser(@RequestHeader("Authorization") String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            String email = jwtService.extractUsername(token);
            Utilisateur user = authService.findUserByEmail(email);
            if (user != null) {
                String roleName = user.getRole() != null ? user.getRole().name() : user.getClass().getSimpleName().toUpperCase();
                return ResponseEntity.ok(Map.of(
                    "id", user.getId(),
                    "email", user.getEmail(),
                    "nom", user.getNom(),
                    "role", roleName
                ));
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}