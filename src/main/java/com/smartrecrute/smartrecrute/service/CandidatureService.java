package com.smartrecrute.smartrecrute.service;

import com.smartrecrute.smartrecrute.entity.Candidature;
import java.util.List;

public interface CandidatureService {
    List<Candidature> getAll();
    Candidature getById(Long id);
    Candidature create(Candidature candidature);
    Candidature update(Long id, Candidature candidature);
    void delete(Long id);
}