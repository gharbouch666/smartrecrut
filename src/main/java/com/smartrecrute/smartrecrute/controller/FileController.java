package com.smartrecrute.smartrecrute.controller;

import com.smartrecrute.smartrecrute.entity.Candidat;
import com.smartrecrute.smartrecrute.entity.Utilisateur;
import com.smartrecrute.smartrecrute.jwt.JwtService;
import com.smartrecrute.smartrecrute.service.AuthService;
import com.smartrecrute.smartrecrute.service.CandidatService;
import com.smartrecrute.smartrecrute.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/files")
public class FileController {

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private CandidatService candidatService;

    @Autowired
    private AuthService authService;

    @Autowired
    private JwtService jwtService;

    @PostMapping("/upload-cv")
    public ResponseEntity<String> uploadCV(@RequestHeader(value = "Authorization", required = false) String authHeader,
                                           @RequestParam("file") MultipartFile file,
                                           @RequestParam(value = "candidatId", required = false) Long candidatId) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Missing or invalid Authorization header");
        }
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Please select a file");
        }
        
        if (!"application/pdf".equals(file.getContentType())) {
            return ResponseEntity.badRequest().body("Only PDF files are allowed");
        }

        try {
            String token = authHeader.substring(7);
            String email = jwtService.extractUsername(token);
            Utilisateur user = authService.findUserByEmail(email);
            if (!(user instanceof Candidat)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only candidates can upload CV files");
            }
            Candidat candidat = (Candidat) user;
            Long effectiveCandidatId = candidatId != null ? candidatId : candidat.getId();
            if (!effectiveCandidatId.equals(candidat.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Cannot upload files for another candidate");
            }

            if (candidat.getCvUrl() != null && !candidat.getCvUrl().isEmpty()) {
                try { fileStorageService.deleteFile(candidat.getCvUrl()); } catch (IOException ignored) {}
            }
            String filename = fileStorageService.storeFile(file, "cv");
            candidatService.updateCvUrl(candidat.getId(), filename);
            
            return ResponseEntity.ok("CV uploaded successfully");
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("Failed to upload file: " + e.getMessage());
        }
    }

    @PostMapping("/upload-lettre")
    public ResponseEntity<String> uploadLettreMotivation(@RequestHeader(value = "Authorization", required = false) String authHeader,
                                                          @RequestParam("file") MultipartFile file,
                                                          @RequestParam(value = "candidatId", required = false) Long candidatId) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Missing or invalid Authorization header");
        }
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Please select a file");
        }
        
        if (!"application/pdf".equals(file.getContentType())) {
            return ResponseEntity.badRequest().body("Only PDF files are allowed");
        }

        try {
            String token = authHeader.substring(7);
            String email = jwtService.extractUsername(token);
            Utilisateur user = authService.findUserByEmail(email);
            if (!(user instanceof Candidat)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only candidates can upload cover letters");
            }
            Candidat candidat = (Candidat) user;
            Long effectiveCandidatId = candidatId != null ? candidatId : candidat.getId();
            if (!effectiveCandidatId.equals(candidat.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Cannot upload files for another candidate");
            }

            if (candidat.getLettreMotivationUrl() != null && !candidat.getLettreMotivationUrl().isEmpty()) {
                try { fileStorageService.deleteFile(candidat.getLettreMotivationUrl()); } catch (IOException ignored) {}
            }
            String filename = fileStorageService.storeFile(file, "lettre");
            candidatService.updateLettreMotivationUrl(candidat.getId(), filename);
            
            return ResponseEntity.ok("Cover letter uploaded successfully");
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("Failed to upload file: " + e.getMessage());
        }
    }

    @GetMapping("/cv/{filename}")
    public ResponseEntity<byte[]> downloadCV(@PathVariable String filename) {
        try {
            byte[] file = fileStorageService.loadFileAsBytes(filename);
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .body(file);
        } catch (IOException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/lettre/{filename}")
    public ResponseEntity<byte[]> downloadLettre(@PathVariable String filename) {
        try {
            byte[] file = fileStorageService.loadFileAsBytes(filename);
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .body(file);
        } catch (IOException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/cv/{filename}")
    public ResponseEntity<String> deleteCv(@PathVariable String filename, @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.startsWith("Bearer ") ? authHeader.substring(7) : authHeader;
            String email = jwtService.extractUsername(token);
            Utilisateur user = authService.findUserByEmail(email);
            if (!(user instanceof Candidat)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only candidates can delete CV files");
            }
            Candidat candidat = (Candidat) user;
            if (!filename.equals(candidat.getCvUrl())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("File does not belong to current candidate");
            }
            fileStorageService.deleteFile(filename);
            candidatService.updateCvUrl(candidat.getId(), null);
            return ResponseEntity.ok("CV deleted successfully");
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("Failed to delete CV: " + e.getMessage());
        }
    }

    @DeleteMapping("/lettre/{filename}")
    public ResponseEntity<String> deleteLettre(@PathVariable String filename, @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.startsWith("Bearer ") ? authHeader.substring(7) : authHeader;
            String email = jwtService.extractUsername(token);
            Utilisateur user = authService.findUserByEmail(email);
            if (!(user instanceof Candidat)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only candidates can delete cover letters");
            }
            Candidat candidat = (Candidat) user;
            if (!filename.equals(candidat.getLettreMotivationUrl())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("File does not belong to current candidate");
            }
            fileStorageService.deleteFile(filename);
            candidatService.updateLettreMotivationUrl(candidat.getId(), null);
            return ResponseEntity.ok("Cover letter deleted successfully");
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("Failed to delete cover letter: " + e.getMessage());
        }
    }
}
