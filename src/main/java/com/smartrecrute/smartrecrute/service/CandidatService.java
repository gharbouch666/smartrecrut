package com.smartrecrute.smartrecrute.service;

import com.smartrecrute.smartrecrute.utilisateur.Candidat;
import java.util.List;

public interface CandidatService {
    List<Candidat> getAll();
    Candidat getById(Long id);
    Candidat create(Candidat candidat);
    Candidat update(Long id, Candidat candidat);
    void delete(Long id);
    Candidat findByEmail(String email);
}