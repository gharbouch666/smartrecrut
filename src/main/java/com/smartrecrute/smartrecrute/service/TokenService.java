package com.smartrecrute.smartrecrute.service;

import com.smartrecrute.smartrecrute.entity.PasswordResetToken;
import com.smartrecrute.smartrecrute.entity.Utilisateur;
import com.smartrecrute.smartrecrute.repository.PasswordResetTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Optional;
import java.util.Random;

@Service
public class TokenService {

    @Autowired
    private PasswordResetTokenRepository tokenRepository;

    private static final int CODE_LENGTH = 6;
    private static final int CODE_EXPIRY_MINUTES = 15;
    private final Random random = new Random();

    public String generateCode() {
        // Generate 6-digit numeric code
        int code = random.nextInt((int) Math.pow(10, CODE_LENGTH));
        return String.format("%06d", code);
    }

    @Transactional
    public void saveToken(Utilisateur utilisateur, String code) {
        code = code.trim();
        System.out.println("[TokenService] saveToken called for user ID: " + utilisateur.getId() + ", email: " + utilisateur.getEmail() + ", code: " + code);
        
        // Try to find existing token for this user
        Optional<PasswordResetToken> existing = tokenRepository.findByUtilisateur(utilisateur);
        Date expiryDate = new Date(System.currentTimeMillis() + CODE_EXPIRY_MINUTES * 60 * 1000);
        
        if (existing.isPresent()) {
            System.out.println("[TokenService] Updating existing token");
            PasswordResetToken token = existing.get();
            token.setToken(code);
            token.setExpiryDate(expiryDate);
            tokenRepository.save(token);
        } else {
            System.out.println("[TokenService] Creating new token");
            PasswordResetToken resetToken = new PasswordResetToken();
            resetToken.setUtilisateur(utilisateur);
            resetToken.setToken(code);
            resetToken.setExpiryDate(expiryDate);
            tokenRepository.save(resetToken);
        }
        
        System.out.println("[TokenService] Token saved successfully");
    }

    public boolean validateCode(String code) {
        code = code.trim();
        Optional<PasswordResetToken> resetToken = tokenRepository.findByToken(code);
        if (resetToken.isPresent()) {
            return !resetToken.get().getExpiryDate().before(new Date());
        }
        return false;
    }

    public Utilisateur getUtilisateurByCode(String code) {
        code = code.trim();
        Optional<PasswordResetToken> resetToken = tokenRepository.findByToken(code);
        return resetToken.map(PasswordResetToken::getUtilisateur).orElse(null);
    }

    @Transactional
    public void deleteCode(String code) {
        code = code.trim();
        tokenRepository.deleteByToken(code);
    }
}