package com.smartrecrute.smartrecrute.repository;

import com.smartrecrute.smartrecrute.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {
    Tag findByLibelle(String libelle);

    @Query("SELECT CASE WHEN COUNT(t) > 0 THEN true ELSE false END FROM Tag t WHERE LOWER(t.libelle) = LOWER(?1)")
    boolean existsByLibelleIgnoreCase(String libelle);

    @Query("SELECT CASE WHEN COUNT(t) > 0 THEN true ELSE false END FROM Tag t WHERE LOWER(t.libelle) = LOWER(?1) AND t.id != ?2")
    boolean existsByLibelleIgnoreCaseAndIdNot(String libelle, Long id);
}