package com.smartrecrute.smartrecrute.admin;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface adminrepo extends JpaRepository<admin, Long> {
	admin findByUsername(String username);
}
