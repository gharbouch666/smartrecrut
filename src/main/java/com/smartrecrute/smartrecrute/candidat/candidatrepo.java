package com.smartrecrute.smartrecrute.candidat;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface candidatrepo extends JpaRepository<candidat, Long> {
	candidat findByEmail(String email);
}
