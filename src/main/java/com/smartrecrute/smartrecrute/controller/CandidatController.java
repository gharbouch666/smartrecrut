package com.smartrecrute.smartrecrute.controller;

import com.smartrecrute.smartrecrute.service.CandidatService;
import com.smartrecrute.smartrecrute.entity.Candidat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/candidats")
public class CandidatController {

    @Autowired
    private CandidatService service;

    @GetMapping
    public ResponseEntity<List<Candidat>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Candidat> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PostMapping
    public ResponseEntity<Candidat> create(@RequestBody Candidat candidat) {
        return ResponseEntity.status(201).body(service.create(candidat));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Candidat> update(@PathVariable Long id, @RequestBody Candidat candidat) {
        return ResponseEntity.ok(service.update(id, candidat));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}