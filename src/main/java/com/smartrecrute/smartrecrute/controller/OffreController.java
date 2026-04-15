package com.smartrecrute.smartrecrute.controller;

import com.smartrecrute.smartrecrute.service.OffreService;
import com.smartrecrute.smartrecrute.entity.Offre;
import com.smartrecrute.smartrecrute.entity.TagOffre;
import com.smartrecrute.smartrecrute.dto.TagOffreRequest;
import com.smartrecrute.smartrecrute.dto.OffreWithTagsRequest;
import com.smartrecrute.smartrecrute.repository.TagOffreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/offres")
public class OffreController {

    @Autowired
    private OffreService service;

    @Autowired
    private TagOffreRepository tagOffreRepository;

    @GetMapping
    public ResponseEntity<List<Offre>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/with-tags")
    public ResponseEntity<List<Map<String, Object>>> getAllWithTags() {
        List<Offre> offres = service.getAll();
        List<Map<String, Object>> result = new ArrayList<>();
        for (Offre offre : offres) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", offre.getId());
            map.put("titre", offre.getTitre());
            map.put("description", offre.getDescription());
            map.put("localisation", offre.getLocalisation());
            map.put("typeContrat", offre.getTypeContrat());
            map.put("departement", offre.getDepartement());
            map.put("statut", offre.getStatut());
            map.put("scoreMinimum", offre.getScoreMinimum());
            List<TagOffre> tags = tagOffreRepository.findByOffre(offre);
            map.put("tags", tags);
            result.add(map);
        }
        return ResponseEntity.ok(result);
    }

    @GetMapping("/open")
    public ResponseEntity<List<Offre>> getOpen() {
        return ResponseEntity.ok(service.getOpenJobs());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Offre> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('RECRUTEUR', 'ADMINISTRATEUR')")
    public ResponseEntity<Offre> create(@RequestBody Offre offre) {
        return ResponseEntity.status(201).body(service.create(offre));
    }

    @PostMapping("/with-tags")
    @PreAuthorize("hasAnyRole('RECRUTEUR', 'ADMINISTRATEUR')")
    public ResponseEntity<?> createWithTags(@RequestBody OffreWithTagsRequest request) {
        Offre saved = service.createWithTags(request.getOffre(), request.getTags());
        return ResponseEntity.status(201).body(Map.of("id", saved.getId(), "titre", saved.getTitre()));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('RECRUTEUR', 'ADMINISTRATEUR')")
    public ResponseEntity<Offre> update(@PathVariable Long id, @RequestBody Offre offre) {
        return ResponseEntity.ok(service.update(id, offre));
    }

    @PutMapping("/{id}/with-tags")
    @PreAuthorize("hasAnyRole('RECRUTEUR', 'ADMINISTRATEUR')")
    public ResponseEntity<Offre> updateWithTags(@PathVariable Long id, @RequestBody OffreWithTagsRequest request) {
        return ResponseEntity.ok(service.updateWithTags(id, request.getOffre(), request.getTags()));
    }

    @GetMapping("/{id}/tags")
    public ResponseEntity<List<TagOffre>> getTags(@PathVariable Long id) {
        return ResponseEntity.ok(service.getTagsByOffre(id));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('RECRUTEUR', 'ADMINISTRATEUR')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}