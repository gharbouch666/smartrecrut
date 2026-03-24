package com.smartrecrute.smartrecrute.rh;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface rhrepo extends JpaRepository<rh, Long> {
	rh findByEmail(String email);
}
