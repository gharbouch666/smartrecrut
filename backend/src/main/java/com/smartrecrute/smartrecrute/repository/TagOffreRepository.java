package com.smartrecrute.smartrecrute.repository;

import com.smartrecrute.smartrecrute.entity.TagOffre;
import com.smartrecrute.smartrecrute.entity.Offre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TagOffreRepository extends JpaRepository<TagOffre, Long> {
    List<TagOffre> findByOffre(Offre offre);
    List<TagOffre> findByOffreId(Long offreId);
    List<TagOffre> findByOffreIdAndObligatoireTrue(Long offreId);
    void deleteByOffreId(Long offreId);
    
    @Query("SELECT COUNT(to) FROM TagOffre to WHERE to.tag.id = ?1")
    long countByTagId(Long tagId);
    
    @Query("SELECT CASE WHEN COUNT(to) > 0 THEN true ELSE false END FROM TagOffre to WHERE to.tag.id = ?1")
    boolean existsByTagId(Long tagId);
}