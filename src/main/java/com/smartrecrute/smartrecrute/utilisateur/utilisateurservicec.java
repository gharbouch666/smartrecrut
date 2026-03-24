package com.smartrecrute.smartrecrute.utilisateur;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class utilisateurservicec implements utilisateurservice {

	private final utilisateurrepo repo;

	@Autowired
	public utilisateurservicec(utilisateurrepo repo) {
		this.repo = repo;
	}

	@Override
	public List<utilisateur> getAll() {
		return repo.findAll();
	}

	@Override
	public utilisateur getById(Long id) {
		return repo.findById(id).orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
	}

	@Override
	public utilisateur create(utilisateur user) {
		return repo.save(user);
	}

	@Override
	public utilisateur update(Long id, utilisateur user) {
		utilisateur existing = getById(id);
		existing.setUsername(user.getUsername());
		existing.setEmail(user.getEmail());
		if (user.getPassword() != null && !user.getPassword().isEmpty()) {
			existing.setPassword(user.getPassword());
		}
		existing.setRole(user.getRole());
		return repo.save(existing);
	}

	@Override
	public void delete(Long id) {
		repo.deleteById(id);
	}

	@Override
	public utilisateur findByUsername(String username) {
		return repo.findByUsername(username);
	}
}
