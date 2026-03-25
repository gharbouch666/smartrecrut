package com.smartrecrute.smartrecrute.service;

import com.smartrecrute.smartrecrute.repository.OffreRepository;
import com.smartrecrute.smartrecrute.offre.Offre;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class OffreServiceImpl implements OffreService {

    @Autowired
    private OffreRepository repository;

    @Override
    public List<Offre> getAll() {
        return repository.findAll();
    }

    @Override
    public Offre getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Offre not found with id: " + id));
    }

    @Override
    public Offre create(Offre offre) {
        return repository.save(offre);
    }

    @Override
    public Offre update(Long id, Offre offreDetails) {
        Offre offre = getById(id);
        offre.setTitre(offreDetails.getTitre());
        offre.setDescription(offreDetails.getDescription());
        offre.setTypeContrat(offreDetails.getTypeContrat());
        offre.setDepartement(offreDetails.getDepartement());
        offre.setLocalisation(offreDetails.getLocalisation());
        offre.setExperienceRequise(offreDetails.getExperienceRequise());
        offre.setAvantages(offreDetails.getAvantages());
        offre.setScoreMinimum(offreDetails.getScoreMinimum());
        offre.setNbCandidatsMax(offreDetails.getNbCandidatsMax());
        offre.setDateCloture(offreDetails.getDateCloture());
        offre.setStatut(offreDetails.getStatut());
        return repository.save(offre);
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }
}