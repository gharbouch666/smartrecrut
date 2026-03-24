package com.smartrecrute.smartrecrute.admin;

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
@RequestMapping("/api/admins")
public class admincontroll {

	private final adminservice service;

	@Autowired
	public admincontroll(adminservice service) {
		this.service = service;
	}

	@GetMapping
	public List<admin> all() {
		return service.getAll();
	}

	@GetMapping("/{id}")
	public admin one(@PathVariable Long id) {
		return service.getById(id);
	}

	@PostMapping
	public ResponseEntity<admin> create(@RequestBody admin admin) {
		return new ResponseEntity<>(service.create(admin), HttpStatus.CREATED);
	}

	@PutMapping("/{id}")
	public admin update(@PathVariable Long id, @RequestBody admin admin) {
		return service.update(id, admin);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		service.delete(id);
		return ResponseEntity.noContent().build();
	}
}
