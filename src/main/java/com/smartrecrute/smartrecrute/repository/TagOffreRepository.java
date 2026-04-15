package com.smartrecrute.smartrecrute.repository;

import com.smartrecrute.smartrecrute.entity.TagOffre;
import com.smartrecrute.smartrecrute.entity.Offre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TagOffreRepository extends JpaRepository<TagOffre, Long> {
    List<TagOffre> findByOffre(Offre offre);
    List<TagOffre> findByOffreId(Long offreId);
    List<TagOffre> findByOffreIdAndObligatoireTrue(Long offreId);
    void deleteByOffreId(Long offreId);
}