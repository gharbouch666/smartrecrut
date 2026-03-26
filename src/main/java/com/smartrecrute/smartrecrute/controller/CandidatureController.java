package com.smartrecrute.smartrecrute.controller;

import com.smartrecrute.smartrecrute.service.CandidatureService;
import com.smartrecrute.smartrecrute.entity.Candidature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/candidatures")
public class CandidatureController {

    @Autowired
    private CandidatureService service;

    @GetMapping
    public ResponseEntity<List<Candidature>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Candidature> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PostMapping
    public ResponseEntity<Candidature> create(@RequestBody Candidature candidature) {
        return ResponseEntity.status(201).body(service.create(candidature));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Candidature> update(@PathVariable Long id, @RequestBody Candidature candidature) {
        return ResponseEntity.ok(service.update(id, candidature));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}