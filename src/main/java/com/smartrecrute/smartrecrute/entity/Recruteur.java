package com.smartrecrute.smartrecrute.entity;

import com.smartrecrute.smartrecrute.enums.Role;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "recruteur")
@Getter
@Setter
@NoArgsConstructor
public class Recruteur extends Utilisateur {

    private String poste;
}