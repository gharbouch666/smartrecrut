package com.smartrecrute.smartrecrute.repository;

import com.smartrecrute.smartrecrute.entity.ProfilTag;
import com.smartrecrute.smartrecrute.entity.Candidat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProfilTagRepository extends JpaRepository<ProfilTag, Long> {
    List<ProfilTag> findByCandidat(Candidat candidat);
    List<ProfilTag> findByCandidatId(Long candidatId);
    Optional<ProfilTag> findByCandidatIdAndTagId(Long candidatId, Long tagId);
    void deleteByCandidatId(Long candidatId);
    
    @Query("SELECT COUNT(p) FROM ProfilTag p WHERE p.tag.id = ?1")
    long countByTagId(Long tagId);
    
    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM ProfilTag p WHERE p.tag.id = ?1")
    boolean existsByTagId(Long tagId);
    
    @Query("SELECT t.libelle, COUNT(p) FROM Tag t JOIN ProfilTag p ON t.id = p.tag.id GROUP BY t.libelle ORDER BY COUNT(p) DESC")
    List<Object[]> countTagsByUsage();
}