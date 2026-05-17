package com.smartrecrute.smartrecrute.repository;

import com.smartrecrute.smartrecrute.entity.PasswordResetToken;
import com.smartrecrute.smartrecrute.entity.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByToken(String token);
    void deleteByToken(String token);
    Optional<PasswordResetToken> findByUtilisateur(Utilisateur utilisateur);
}