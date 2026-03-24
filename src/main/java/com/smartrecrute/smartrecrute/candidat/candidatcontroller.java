package com.smartrecrute.smartrecrute.candidat;

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
@RequestMapping("/api/candidats")
public class candidatcontroller {

	private final candidatservice service;

	@Autowired
	public candidatcontroller(candidatservice service) {
		this.service = service;
	}

	@GetMapping
	public List<candidat> all() {
		return service.getAll();
	}

	@GetMapping("/{id}")
	public candidat one(@PathVariable Long id) {
		return service.getById(id);
	}

	@PostMapping
	public ResponseEntity<candidat> create(@RequestBody candidat candidat) {
		return new ResponseEntity<>(service.create(candidat), HttpStatus.CREATED);
	}

	@PutMapping("/{id}")
	public candidat update(@PathVariable Long id, @RequestBody candidat candidat) {
		return service.update(id, candidat);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		service.delete(id);
		return ResponseEntity.noContent().build();
	}
}
