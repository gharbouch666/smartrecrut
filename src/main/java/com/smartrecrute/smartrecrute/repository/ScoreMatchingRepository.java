package com.smartrecrute.smartrecrute.repository;

import com.smartrecrute.smartrecrute.candidature.ScoreMatching;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScoreMatchingRepository extends JpaRepository<ScoreMatching, Long> {
}