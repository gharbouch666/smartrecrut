package com.smartrecrute.smartrecrute.dto;

import com.smartrecrute.smartrecrute.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserRegistrationRequest {
    private String nom;
    private String email;
    private String motDePasse;
    private Role role;
}