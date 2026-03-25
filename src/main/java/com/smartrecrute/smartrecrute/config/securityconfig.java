package com.smartrecrute.smartrecrute.config;

import com.smartrecrute.smartrecrute.repository.AdministrateurRepository;
import com.smartrecrute.smartrecrute.repository.CandidatRepository;
import com.smartrecrute.smartrecrute.repository.RecruteurRepository;
import com.smartrecrute.smartrecrute.utilisateur.Administrateur;
import com.smartrecrute.smartrecrute.utilisateur.Candidat;
import com.smartrecrute.smartrecrute.utilisateur.Recruteur;
import com.smartrecrute.smartrecrute.utilisateur.Utilisateur;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
public class securityconfig {

	private final AdministrateurRepository adminRepo;
	private final CandidatRepository candidatRepo;
	private final RecruteurRepository recruteurRepo;

	public securityconfig(AdministrateurRepository adminRepo, CandidatRepository candidatRepo, RecruteurRepository recruteurRepo) {
		this.adminRepo = adminRepo;
		this.candidatRepo = candidatRepo;
		this.recruteurRepo = recruteurRepo;
	}

	@Bean
	public UserDetailsService userDetailsService() {
		return email -> {
			// Try to find user by email in all repositories
			Administrateur admin = adminRepo.findByEmail(email);
			if (admin != null) {
				return createUserDetails(admin.getEmail(), admin.getMotDePasse(), admin.getRole().name());
			}

			Candidat candidat = candidatRepo.findByEmail(email);
			if (candidat != null) {
				return createUserDetails(candidat.getEmail(), candidat.getMotDePasse(), candidat.getRole().name());
			}

			Recruteur recruteur = recruteurRepo.findByEmail(email);
			if (recruteur != null) {
				return createUserDetails(recruteur.getEmail(), recruteur.getMotDePasse(), recruteur.getRole().name());
			}

			throw new UsernameNotFoundException("Utilisateur non trouvé avec l'email: " + email);
		};
	}

	private UserDetails createUserDetails(String email, String password, String role) {
		return User.withUsername(email)
			.password(password)
			.roles(role)
			.build();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return cryptpassword.passwordEncoder();
	}

	@Bean
	public AuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
		authProvider.setUserDetailsService(userDetailsService());
		authProvider.setPasswordEncoder(passwordEncoder());
		return authProvider;
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
		return config.getAuthenticationManager();
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
			.csrf().disable()
			.authorizeHttpRequests()
			.requestMatchers("/api/auth/**", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
			.requestMatchers("/api/administrateurs/**").hasRole("ADMIN")
			.requestMatchers("/api/recruteurs/**").hasAnyRole("ADMIN", "RECRUTEUR")
			.anyRequest().authenticated()
			.and().httpBasic();
		return http.build();
	}
}
