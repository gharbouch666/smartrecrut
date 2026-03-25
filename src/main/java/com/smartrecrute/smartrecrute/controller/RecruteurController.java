package com.smartrecrute.smartrecrute.controller;

import com.smartrecrute.smartrecrute.service.RecruteurService;
import com.smartrecrute.smartrecrute.utilisateur.Recruteur;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/recruteurs")
public class RecruteurController {

    @Autowired
    private RecruteurService service;

    @GetMapping
    public ResponseEntity<List<Recruteur>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Recruteur> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PostMapping
    public ResponseEntity<Recruteur> create(@RequestBody Recruteur recruteur) {
        return ResponseEntity.status(201).body(service.create(recruteur));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Recruteur> update(@PathVariable Long id, @RequestBody Recruteur recruteur) {
        return ResponseEntity.ok(service.update(id, recruteur));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}