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
        return ResponseEntity.ok(jwtService.generate(authentification.username()));
    }

    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(@RequestParam String email, @RequestBody ChangePasswordRequest request) {
        authService.changePassword(email, request);
        return ResponseEntity.ok("Password changed successfully");
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        authService.forgotPassword(request);
        return ResponseEntity.ok("Password reset email sent");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request);
        return ResponseEntity.ok("Password reset successfully");
    }
}