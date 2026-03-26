package com.smartrecrute.smartrecrute.repository;

import com.smartrecrute.smartrecrute.entity.Recruteur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecruteurRepository extends JpaRepository<Recruteur, Long> {
    Recruteur findByEmail(String email);
}