package com.smartrecrute.smartrecrute.admin;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class adminservicec implements adminservice {

	private final adminrepo repo;

	@Autowired
	public adminservicec(adminrepo repo) {
		this.repo = repo;
	}

	@Override
	public List<admin> getAll() {
		return repo.findAll();
	}

	@Override
	public admin getById(Long id) {
		return repo.findById(id).orElseThrow(() -> new RuntimeException("Admin non trouvé"));
	}

	@Override
	public admin create(admin admin) {
		return repo.save(admin);
	}

	@Override
	public admin update(Long id, admin input) {
		admin existing = getById(id);
		existing.setUsername(input.getUsername());
		existing.setEmail(input.getEmail());
		if (input.getPassword() != null && !input.getPassword().isEmpty()) {
			existing.setPassword(input.getPassword());
		}
		return repo.save(existing);
	}

	@Override
	public void delete(Long id) {
		repo.deleteById(id);
	}

	@Override
	public admin findByUsername(String username) {
		return repo.findByUsername(username);
	}
}
