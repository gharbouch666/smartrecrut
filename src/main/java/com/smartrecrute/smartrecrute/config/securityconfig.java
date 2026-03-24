package com.smartrecrute.smartrecrute.config;

import com.smartrecrute.smartrecrute.utilisateur.utilisateur;
import com.smartrecrute.smartrecrute.utilisateur.utilisateurrepo;
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

	private final utilisateurrepo repo;

	public securityconfig(utilisateurrepo repo) {
		this.repo = repo;
	}

	@Bean
	public UserDetailsService userDetailsService() {
		return username -> {
			utilisateur user = repo.findByUsername(username);
			if (user == null) {
				throw new UsernameNotFoundException("Utilisateur non trouvé");
			}
			UserDetails userDetails = User.withUsername(user.getUsername())
				.password(user.getPassword())
				.roles(user.getRole().name())
				.build();
			return userDetails;
		};
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
			.anyRequest().authenticated()
			.and().httpBasic();
		return http.build();
	}
}
