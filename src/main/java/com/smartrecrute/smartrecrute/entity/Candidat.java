package com.smartrecrute.smartrecrute.entity;

import com.smartrecrute.smartrecrute.enums.Role;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;

@Entity
@Table(name = "candidat")
@Getter
@Setter
@NoArgsConstructor
public class Candidat extends Utilisateur {

    private LocalDate dateNaissance;
    private String telephone;
    private String cvUrl;
}