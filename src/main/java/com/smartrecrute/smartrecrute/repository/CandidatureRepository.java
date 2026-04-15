package com.smartrecrute.smartrecrute.repository;

import com.smartrecrute.smartrecrute.entity.Candidature;
import com.smartrecrute.smartrecrute.entity.Offre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CandidatureRepository extends JpaRepository<Candidature, Long> {
    List<Candidature> findByOffre(Offre offre);
    List<Candidature> findByOffreId(Long offreId);
    List<Candidature> findByCandidatId(Long candidatId);
    
    // Filtered in service instead
    List<Candidature> findAll();
}