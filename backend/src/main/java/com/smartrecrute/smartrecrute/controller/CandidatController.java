package com.smartrecrute.smartrecrute.controller;

import com.smartrecrute.smartrecrute.service.CandidatService;
import com.smartrecrute.smartrecrute.entity.Candidat;
import com.smartrecrute.smartrecrute.entity.ProfilTag;
import com.smartrecrute.smartrecrute.dto.ProfilTagRequest;
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
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Candidat candidat) {
        try {
            return ResponseEntity.ok(service.update(id, candidat));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/competences")
    public ResponseEntity<List<ProfilTag>> getCompetences(@PathVariable Long id) {
        return ResponseEntity.ok(service.getSkillsByCandidat(id));
    }

    @PutMapping("/{id}/competences")
    public ResponseEntity<Candidat> updateCompetences(@PathVariable Long id, @RequestBody List<ProfilTagRequest> competences) {
        return ResponseEntity.ok(service.updateCompetences(id, competences));
    }

    @PostMapping("/{id}/competences")
    public ResponseEntity<ProfilTag> addCompetence(@PathVariable Long id, @RequestBody ProfilTagRequest request) {
        return ResponseEntity.ok(service.addSkill(id, request));
    }

    @DeleteMapping("/{id}/competences/{tagId}")
    public ResponseEntity<Void> removeCompetence(@PathVariable Long id, @PathVariable Long tagId) {
        service.removeSkill(id, tagId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/search")
    public ResponseEntity<List<Candidat>> searchCandidates(@RequestBody SearchRequest request) {
        return ResponseEntity.ok(service.searchCandidates(request.query));
    }

    public static class SearchRequest {
        private String query;
        public String getQuery() { return query; }
        public void setQuery(String query) { this.query = query; }
    }
}