package com.smartrecrute.smartrecrute.auth;

import com.smartrecrute.smartrecrute.utilisateur.role;
import com.smartrecrute.smartrecrute.utilisateur.utilisateur;
import com.smartrecrute.smartrecrute.utilisateur.utilisateurservice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class authcontroller {

	private final utilisateurservice service;
	private final PasswordEncoder encoder;

	@Autowired
	public authcontroller(utilisateurservice service, PasswordEncoder encoder) {
		this.service = service;
		this.encoder = encoder;
	}

	@PostMapping("/signup")
	public ResponseEntity<utilisateur> signup(@RequestBody utilisateur user) {
		user.setPassword(encoder.encode(user.getPassword()));
		user.setRole(role.USER);
		return ResponseEntity.ok(service.create(user));
	}
}