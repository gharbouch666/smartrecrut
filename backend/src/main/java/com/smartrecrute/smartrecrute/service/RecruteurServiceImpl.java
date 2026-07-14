package com.smartrecrute.smartrecrute.service;

import com.smartrecrute.smartrecrute.repository.RecruteurRepository;
import com.smartrecrute.smartrecrute.entity.Recruteur;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class RecruteurServiceImpl implements RecruteurService {

    @Autowired
    private RecruteurRepository repository;

    @Override
    public List<Recruteur> getAll() {
        return repository.findAll();
    }

    @Override
    public Recruteur getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Recruteur not found with id: " + id));
    }

    @Override
    public Recruteur create(Recruteur recruteur) {
        return repository.save(recruteur);
    }

    @Override
    public Recruteur update(Long id, Recruteur recruteurDetails) {
        Recruteur recruteur = getById(id);
        recruteur.setNom(recruteurDetails.getNom());
        recruteur.setEmail(recruteurDetails.getEmail());
        recruteur.setPoste(recruteurDetails.getPoste());
        if (recruteurDetails.getMotDePasse() != null && !recruteurDetails.getMotDePasse().isEmpty()) {
            recruteur.setMotDePasse(recruteurDetails.getMotDePasse());
        }
        return repository.save(recruteur);
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }

    @Override
    public Recruteur findByEmail(String email) {
        return repository.findByEmail(email);
    }
}