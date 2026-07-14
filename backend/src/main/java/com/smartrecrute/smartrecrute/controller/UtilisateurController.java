package com.smartrecrute.smartrecrute.controller;

import com.smartrecrute.smartrecrute.entity.Utilisateur;
import com.smartrecrute.smartrecrute.jwt.JwtService;
import com.smartrecrute.smartrecrute.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/utilisateurs")
public class UtilisateurController {

    @Autowired
    private AuthService authService;

    @Autowired
    private JwtService jwtService;

    @GetMapping("/profile")
    public ResponseEntity<Map<String, Object>> getProfile(@RequestHeader("Authorization") String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            String email = jwtService.extractUsername(token);
            Utilisateur user = authService.findUserByEmail(email);
            if (user != null) {
                return ResponseEntity.ok(Map.of(
                        "id", user.getId(),
                        "email", user.getEmail(),
                        "nom", user.getNom(),
                        "role", user.getRole() != null ? user.getRole().name() : ""
                ));
            }
        }
        return ResponseEntity.status(401).build();
    }

    @PutMapping("/profile")
    public ResponseEntity<Map<String, Object>> updateProfile(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody Map<String, String> request
    ) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).build();
        }
        String token = authHeader.substring(7);
        String email = jwtService.extractUsername(token);
        String nom = request.get("nom");
        String newEmail = request.get("email");
        Utilisateur user = authService.updateProfile(email, nom, newEmail);
        return ResponseEntity.ok(Map.of(
                "id", user.getId(),
                "email", user.getEmail(),
                "nom", user.getNom(),
                "role", user.getRole() != null ? user.getRole().name() : ""
        ));
    }
}
