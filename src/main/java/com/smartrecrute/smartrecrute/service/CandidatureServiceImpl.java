package com.smartrecrute.smartrecrute.service;

import com.smartrecrute.smartrecrute.repository.CandidatureRepository;
import com.smartrecrute.smartrecrute.entity.Candidature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CandidatureServiceImpl implements CandidatureService {

    @Autowired
    private CandidatureRepository repository;

    @Override
    public List<Candidature> getAll() {
        return repository.findAll();
    }

    @Override
    public Candidature getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Candidature not found with id: " + id));
    }

    @Override
    public Candidature create(Candidature candidature) {
        return repository.save(candidature);
    }

    @Override
    public Candidature update(Long id, Candidature candidatureDetails) {
        Candidature candidature = getById(id);
        candidature.setScoreTotal(candidatureDetails.getScoreTotal());
        candidature.setStatut(candidatureDetails.getStatut());
        return repository.save(candidature);
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }
}