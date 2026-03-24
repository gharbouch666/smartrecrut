package com.smartrecrute.smartrecrute.rh;

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
@RequestMapping("/api/rhs")
public class rhcontroller {

	private final rhservice service;

	@Autowired
	public rhcontroller(rhservice service) {
		this.service = service;
	}

	@GetMapping
	public List<rh> all() {
		return service.getAll();
	}

	@GetMapping("/{id}")
	public rh one(@PathVariable Long id) {
		return service.getById(id);
	}

	@PostMapping
	public ResponseEntity<rh> create(@RequestBody rh rh) {
		return new ResponseEntity<>(service.create(rh), HttpStatus.CREATED);
	}

	@PutMapping("/{id}")
	public rh update(@PathVariable Long id, @RequestBody rh rh) {
		return service.update(id, rh);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		service.delete(id);
		return ResponseEntity.noContent().build();
	}
}
