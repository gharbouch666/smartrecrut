package com.smartrecrute.smartrecrute.repository;

import com.smartrecrute.smartrecrute.utilisateur.Candidat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CandidatRepository extends JpaRepository<Candidat, Long> {
    Candidat findByEmail(String email);
}