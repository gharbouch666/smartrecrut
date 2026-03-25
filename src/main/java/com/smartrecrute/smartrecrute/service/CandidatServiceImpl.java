package com.smartrecrute.smartrecrute.service;

import com.smartrecrute.smartrecrute.repository.CandidatRepository;
import com.smartrecrute.smartrecrute.utilisateur.Candidat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CandidatServiceImpl implements CandidatService {

    @Autowired
    private CandidatRepository repository;

    @Override
    public List<Candidat> getAll() {
        return repository.findAll();
    }

    @Override
    public Candidat getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Candidat not found with id: " + id));
    }

    @Override
    public Candidat create(Candidat candidat) {
        return repository.save(candidat);
    }

    @Override
    public Candidat update(Long id, Candidat candidatDetails) {
        Candidat candidat = getById(id);
        candidat.setNom(candidatDetails.getNom());
        candidat.setEmail(candidatDetails.getEmail());
        candidat.setDateNaissance(candidatDetails.getDateNaissance());
        candidat.setTelephone(candidatDetails.getTelephone());
        candidat.setCvUrl(candidatDetails.getCvUrl());
        if (candidatDetails.getMotDePasse() != null && !candidatDetails.getMotDePasse().isEmpty()) {
            candidat.setMotDePasse(candidatDetails.getMotDePasse());
        }
        return repository.save(candidat);
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }

    @Override
    public Candidat findByEmail(String email) {
        return repository.findByEmail(email);
    }
}