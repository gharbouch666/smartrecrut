package com.smartrecrute.smartrecrute.candidat;

import java.util.List;

public interface candidatservice {
	List<candidat> getAll();
	candidat getById(Long id);
	candidat create(candidat candidat);
	candidat update(Long id, candidat candidat);
	void delete(Long id);
	candidat findByEmail(String email);
}
