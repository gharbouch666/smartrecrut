package com.smartrecrute.smartrecrute.controller;

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

    @PostMapping("/upload-cv")
    public ResponseEntity<String> uploadCV(@RequestParam("file") MultipartFile file, @RequestParam("candidatId") Long candidatId) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Please select a file");
        }
        
        if (!file.getContentType().equals("application/pdf")) {
            return ResponseEntity.badRequest().body("Only PDF files are allowed");
        }

        try {
            String filename = fileStorageService.storeFile(file, "cv");
            candidatService.updateCvUrl(candidatId, filename);
            
            return ResponseEntity.ok("CV uploaded successfully");
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("Failed to upload file: " + e.getMessage());
        }
    }

    @PostMapping("/upload-lettre")
    public ResponseEntity<String> uploadLettreMotivation(@RequestParam("file") MultipartFile file, @RequestParam("candidatId") Long candidatId) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Please select a file");
        }
        
        if (!file.getContentType().equals("application/pdf")) {
            return ResponseEntity.badRequest().body("Only PDF files are allowed");
        }

        try {
            String filename = fileStorageService.storeFile(file, "lettre");
            candidatService.updateLettreMotivationUrl(candidatId, filename);
            
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
}