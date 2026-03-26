package com.smartrecrute.smartrecrute.controller;

import com.smartrecrute.smartrecrute.service.AdministrateurService;
import com.smartrecrute.smartrecrute.entity.Administrateur;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/administrateurs")
public class AdministrateurController {

    @Autowired
    private AdministrateurService service;

    @GetMapping
    public ResponseEntity<List<Administrateur>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Administrateur> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PostMapping
    public ResponseEntity<Administrateur> create(@RequestBody Administrateur administrateur) {
        return ResponseEntity.status(201).body(service.create(administrateur));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Administrateur> update(@PathVariable Long id, @RequestBody Administrateur administrateur) {
        return ResponseEntity.ok(service.update(id, administrateur));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}