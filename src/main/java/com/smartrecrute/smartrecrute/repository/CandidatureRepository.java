package com.smartrecrute.smartrecrute.repository;

import com.smartrecrute.smartrecrute.entity.Candidature;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CandidatureRepository extends JpaRepository<Candidature, Long> {
}