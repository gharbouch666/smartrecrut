package com.smartrecrute.smartrecrute.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.*;
import java.util.UUID;

@Service
public class FileStorageService {

    private final Path fileStorageLocation = Paths.get("uploads").toAbsolutePath().normalize();

    public FileStorageService() {
        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception e) {
            throw new RuntimeException("Could not create upload directory", e);
        }
    }

    public String storeFile(MultipartFile file, String prefix) throws IOException {
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        
        String filename = prefix + "_" + UUID.randomUUID().toString() + extension;
        Path targetLocation = this.fileStorageLocation.resolve(filename);
        
        Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
        
        return filename;
    }

    public byte[] loadFileAsBytes(String filename) throws IOException {
        Path filePath = this.fileStorageLocation.resolve(filename);
        return Files.readAllBytes(filePath);
    }

    public InputStream loadFileAsStream(String filename) throws IOException {
        Path filePath = this.fileStorageLocation.resolve(filename);
        return new FileInputStream(filePath.toFile());
    }

    public Path getFileStorageLocation() {
        return fileStorageLocation;
    }
}