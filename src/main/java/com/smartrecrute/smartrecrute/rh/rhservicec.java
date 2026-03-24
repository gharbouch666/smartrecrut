package com.smartrecrute.smartrecrute.rh;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class rhservicec implements rhservice {

	private final rhrepo repo;

	@Autowired
	public rhservicec(rhrepo repo) {
		this.repo = repo;
	}

	@Override
	public List<rh> getAll() {
		return repo.findAll();
	}

	@Override
	public rh getById(Long id) {
		return repo.findById(id).orElseThrow(() -> new RuntimeException("RH non trouvé"));
	}

	@Override
	public rh create(rh rh) {
		return repo.save(rh);
	}

	@Override
	public rh update(Long id, rh input) {
		rh existing = getById(id);
		existing.setNom(input.getNom());
		existing.setPrenom(input.getPrenom());
		existing.setEmail(input.getEmail());
		existing.setTelephone(input.getTelephone());
		existing.setDepartement(input.getDepartement());
		return repo.save(existing);
	}

	@Override
	public void delete(Long id) {
		repo.deleteById(id);
	}

	@Override
	public rh findByEmail(String email) {
		return repo.findByEmail(email);
	}
}
