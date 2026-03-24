package com.smartrecrute.smartrecrute.config;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class cryptpassword {

	public static PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
