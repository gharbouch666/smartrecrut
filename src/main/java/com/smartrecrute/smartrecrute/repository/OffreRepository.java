package com.smartrecrute.smartrecrute.repository;

import com.smartrecrute.smartrecrute.entity.Offre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OffreRepository extends JpaRepository<Offre, Long> {
}