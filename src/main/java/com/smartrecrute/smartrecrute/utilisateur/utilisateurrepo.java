package com.smartrecrute.smartrecrute.utilisateur;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface utilisateurrepo extends JpaRepository<utilisateur, Long> {
	utilisateur findByUsername(String username);
}
