package com.smartrecrute.smartrecrute.repository;

import com.smartrecrute.smartrecrute.entity.Offre;
import com.smartrecrute.smartrecrute.enums.StatutOffre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface OffreRepository extends JpaRepository<Offre, Long> {
    List<Offre> findByStatut(StatutOffre statut);
    List<Offre> findByRecruteurId(Long recruteurId);
    default List<Offre> findByStatutOuvert() {
        return findByStatut(StatutOffre.OUVERTE);
    }
}