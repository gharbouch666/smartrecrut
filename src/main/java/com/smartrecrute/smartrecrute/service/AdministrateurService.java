package com.smartrecrute.smartrecrute.service;

import com.smartrecrute.smartrecrute.entity.Administrateur;
import java.util.List;

public interface AdministrateurService {
    List<Administrateur> getAll();
    Administrateur getById(Long id);
    Administrateur create(Administrateur administrateur);
    Administrateur update(Long id, Administrateur administrateur);
    void delete(Long id);
    Administrateur findByEmail(String email);
}