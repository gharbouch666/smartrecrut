package com.smartrecrute.smartrecrute.service;

import com.smartrecrute.smartrecrute.repository.AdministrateurRepository;
import com.smartrecrute.smartrecrute.entity.Administrateur;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class AdministrateurServiceImpl implements AdministrateurService {

    @Autowired
    private AdministrateurRepository repository;

    @Override
    public List<Administrateur> getAll() {
        return repository.findAll();
    }

    @Override
    public Administrateur getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Administrateur not found with id: " + id));
    }

    @Override
    public Administrateur create(Administrateur administrateur) {
        return repository.save(administrateur);
    }

    @Override
    public Administrateur update(Long id, Administrateur administrateurDetails) {
        Administrateur administrateur = getById(id);
        administrateur.setNom(administrateurDetails.getNom());
        administrateur.setEmail(administrateurDetails.getEmail());
        if (administrateurDetails.getMotDePasse() != null && !administrateurDetails.getMotDePasse().isEmpty()) {
            administrateur.setMotDePasse(administrateurDetails.getMotDePasse());
        }
        return repository.save(administrateur);
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }

    @Override
    public Administrateur findByEmail(String email) {
        return repository.findByEmail(email);
    }
}