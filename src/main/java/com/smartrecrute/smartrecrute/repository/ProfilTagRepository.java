package com.smartrecrute.smartrecrute.repository;

import com.smartrecrute.smartrecrute.entity.ProfilTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProfilTagRepository extends JpaRepository<ProfilTag, Long> {
}