package com.smartrecrute.smartrecrute.utilisateur;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/utilisateurs")
public class utilisateurcontroller {

	private final utilisateurservice service;

	@Autowired
	public utilisateurcontroller(utilisateurservice service) {
		this.service = service;
	}

	@GetMapping
	public List<utilisateur> all() {
		return service.getAll();
	}

	@GetMapping("/{id}")
	public utilisateur one(@PathVariable Long id) {
		return service.getById(id);
	}

	@PostMapping
	public ResponseEntity<utilisateur> create(@RequestBody utilisateur user) {
		return new ResponseEntity<>(service.create(user), HttpStatus.CREATED);
	}

	@PutMapping("/{id}")
	public utilisateur update(@PathVariable Long id, @RequestBody utilisateur user) {
		return service.update(id, user);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		service.delete(id);
		return ResponseEntity.noContent().build();
	}
}
