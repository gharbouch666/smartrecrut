package com.smartrecrute.smartrecrute.service;

import com.smartrecrute.smartrecrute.offre.Offre;
import java.util.List;

public interface OffreService {
    List<Offre> getAll();
    Offre getById(Long id);
    Offre create(Offre offre);
    Offre update(Long id, Offre offre);
    void delete(Long id);
}