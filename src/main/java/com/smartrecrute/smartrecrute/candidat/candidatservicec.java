package com.smartrecrute.smartrecrute.candidat;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class candidatservicec implements candidatservice {

	private final candidatrepo repo;

	@Autowired
	public candidatservicec(candidatrepo repo) {
		this.repo = repo;
	}

	@Override
	public List<candidat> getAll() {
		return repo.findAll();
	}

	@Override
	public candidat getById(Long id) {
		return repo.findById(id).orElseThrow(() -> new RuntimeException("Candidat non trouvé"));
	}

	@Override
	public candidat create(candidat candidat) {
		return repo.save(candidat);
	}

	@Override
	public candidat update(Long id, candidat input) {
		candidat existing = getById(id);
		existing.setNom(input.getNom());
		existing.setPrenom(input.getPrenom());
		existing.setEmail(input.getEmail());
		existing.setTelephone(input.getTelephone());
		existing.setCv(input.getCv());
		existing.setStatus(input.getStatus());
		return repo.save(existing);
	}

	@Override
	public void delete(Long id) {
		repo.deleteById(id);
	}

	@Override
	public candidat findByEmail(String email) {
		return repo.findByEmail(email);
	}
}
