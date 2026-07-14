package com.smartrecrute.smartrecrute.service;

import com.smartrecrute.smartrecrute.entity.Recruteur;
import java.util.List;

public interface RecruteurService {
    List<Recruteur> getAll();
    Recruteur getById(Long id);
    Recruteur create(Recruteur recruteur);
    Recruteur update(Long id, Recruteur recruteur);
    void delete(Long id);
    Recruteur findByEmail(String email);
}