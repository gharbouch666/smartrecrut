package com.smartrecrute.smartrecrute.repository;

import com.smartrecrute.smartrecrute.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {
    Tag findByLibelle(String libelle);
}