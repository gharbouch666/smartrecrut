package com.smartrecrute.smartrecrute.controller;

import com.smartrecrute.smartrecrute.service.CandidatureService;
import com.smartrecrute.smartrecrute.service.MatchingService;
import com.smartrecrute.smartrecrute.entity.Candidature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/candidatures")
public class CandidatureController {

    @Autowired
    private CandidatureService service;

    @Autowired
    private MatchingService matchingService;

    @GetMapping
    @PreAuthorize("hasAnyRole('RECRUTEUR', 'ADMINISTRATEUR')")
    public ResponseEntity<List<Candidature>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/recruteur/{recruteurId}")
    public ResponseEntity<List<Candidature>> getByRecruteur(@PathVariable Long recruteurId) {
        return ResponseEntity.ok(service.getByRecruteur(recruteurId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Candidature> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping("/offre/{offreId}")
    @PreAuthorize("hasAnyRole('RECRUTEUR', 'ADMINISTRATEUR')")
    public ResponseEntity<List<Candidature>> getByOffre(@PathVariable Long offreId) {
        return ResponseEntity.ok(service.getByOffre(offreId));
    }

    @GetMapping("/offre/{offreId}/ranked")
    @PreAuthorize("hasAnyRole('RECRUTEUR', 'ADMINISTRATEUR')")
    public ResponseEntity<List<Candidature>> getRankedByOffre(@PathVariable Long offreId) {
        return ResponseEntity.ok(service.getRankedByOffre(offreId));
    }

    @GetMapping("/candidat/{candidatId}")
    public ResponseEntity<List<Candidature>> getByCandidat(@PathVariable Long candidatId) {
        return ResponseEntity.ok(service.getByCandidat(candidatId));
    }

    @PostMapping
    public ResponseEntity<Candidature> create(@RequestBody Candidature candidature) {
        return ResponseEntity.status(201).body(service.create(candidature));
    }

    @PostMapping("/apply")
    public ResponseEntity<Candidature> apply(@RequestBody Map<String, Long> request) {
        Long candidatId = request.get("candidatId");
        Long offreId = request.get("offreId");
        return ResponseEntity.status(201).body(service.applyToOffre(candidatId, offreId));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('RECRUTEUR', 'ADMINISTRATEUR')")
    public ResponseEntity<Candidature> update(@PathVariable Long id, @RequestBody Candidature candidature) {
        return ResponseEntity.ok(service.update(id, candidature));
    }

    @PatchMapping("/{id}/statut")
    @PreAuthorize("hasAnyRole('RECRUTEUR', 'ADMINISTRATEUR')")
    public ResponseEntity<Candidature> updateStatut(@PathVariable Long id, @RequestBody Map<String, String> request) {
        return ResponseEntity.ok(service.updateStatut(id, request.get("statut")));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('RECRUTEUR', 'ADMINISTRATEUR')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/score-breakdown/{candidatId}/{offreId}")
    @PreAuthorize("hasAnyRole('RECRUTEUR', 'ADMINISTRATEUR')")
    public ResponseEntity<Map<String, Object>> getScoreBreakdown(
            @PathVariable Long candidatId, @PathVariable Long offreId) {
        Map<String, Object> breakdown = matchingService.getScoreBreakdown(candidatId, offreId);
        return ResponseEntity.ok(breakdown);
    }

    @PostMapping("/recalculate/candidat/{candidatId}")
    public ResponseEntity<Void> recalculateForCandidat(@PathVariable Long candidatId) {
        service.recalculateAllForCandidat(candidatId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/with-skills")
    public ResponseEntity<Map<String, Object>> getCandidatureWithSkills(@PathVariable Long id) {
        return ResponseEntity.ok(service.getCandidatureWithSkills(id));
    }
}