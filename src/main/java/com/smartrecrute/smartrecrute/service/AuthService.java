package com.smartrecrute.smartrecrute.service;

import com.smartrecrute.smartrecrute.dto.ChangePasswordRequest;
import com.smartrecrute.smartrecrute.dto.ForgotPasswordRequest;
import com.smartrecrute.smartrecrute.dto.ResetPasswordRequest;
import com.smartrecrute.smartrecrute.dto.UserRegistrationRequest;
import com.smartrecrute.smartrecrute.enums.Role;
import com.smartrecrute.smartrecrute.entity.Administrateur;
import com.smartrecrute.smartrecrute.entity.Candidat;
import com.smartrecrute.smartrecrute.entity.Recruteur;
import com.smartrecrute.smartrecrute.entity.Utilisateur;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AuthService implements UserDetailsService {

    @Autowired
    private AdministrateurService administrateurService;

    @Autowired
    private CandidatService candidatService;

    @Autowired
    private RecruteurService recruteurService;

    @Autowired
    private PasswordService passwordService;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String mailFrom;

    // DON'T inject passwordEncoder - causes circular dependency!

    public Utilisateur register(UserRegistrationRequest request) {
        // Encode password
        String encodedPassword = passwordService.encodePassword(request.getMotDePasse());

        switch (request.getRole()) {
            case ADMIN:
            case ADMINISTRATEUR:
                Administrateur admin = new Administrateur();
                admin.setNom(request.getNom());
                admin.setEmail(request.getEmail());
                admin.setMotDePasse(encodedPassword);
                admin.setRole(Role.ADMINISTRATEUR);
                return administrateurService.create(admin);
            case CANDIDAT:
                Candidat candidat = new Candidat();
                candidat.setNom(request.getNom());
                candidat.setEmail(request.getEmail());
                candidat.setMotDePasse(encodedPassword);
                candidat.setRole(Role.CANDIDAT);
                return candidatService.create(candidat);
            case RECRUTEUR:
                Recruteur recruteur = new Recruteur();
                recruteur.setNom(request.getNom());
                recruteur.setEmail(request.getEmail());
                recruteur.setMotDePasse(encodedPassword);
                recruteur.setRole(Role.RECRUTEUR);
                return recruteurService.create(recruteur);
            default:
                throw new IllegalArgumentException("Invalid role: " + request.getRole());
        }
    }

    public Utilisateur updateProfile(String email, String nom, String newEmail) {
        Utilisateur user = findUserByEmail(email);
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        if (nom != null && !nom.isEmpty()) {
            user.setNom(nom);
        }
        if (newEmail != null && !newEmail.isEmpty()) {
            user.setEmail(newEmail);
        }
        saveUser(user);
        return user;
    }

    public void setPasswordDirect(String email, String newPassword) {
        Utilisateur user = findUserByEmail(email);
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        String encodedNewPassword = passwordService.encodePassword(newPassword);
        user.setMotDePasse(encodedNewPassword);
        saveUser(user);
    }

    public void changePassword(String email, ChangePasswordRequest request) {
        // Find user by email across all types
        Utilisateur user = findUserByEmail(email);
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        // OLD PASSWORD IS REQUIRED
        if (request.getOldPassword() == null || request.getOldPassword().isEmpty()) {
            throw new RuntimeException("Old password is required");
        }

        // Verify old password using PasswordService (same BCrypt as registration)
        boolean matches = passwordService.matches(request.getOldPassword(), user.getMotDePasse());
        if (!matches) {
            throw new RuntimeException("Old password is incorrect");
        }

        // Check new password confirmation
        if (request.getNewPassword() == null || request.getConfirmPassword() == null) {
            throw new RuntimeException("New password is required");
        }
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new RuntimeException("New passwords do not match");
        }

        // Encode and update password
        String encodedNewPassword = passwordService.encodePassword(request.getNewPassword());
        user.setMotDePasse(encodedNewPassword);

        // Save based on user type
        saveUser(user);
    }

    public void forgotPassword(ForgotPasswordRequest request) {
        if (request.getEmail() == null || request.getEmail().isBlank()) {
            throw new RuntimeException("Email is required");
        }
        Utilisateur user = findUserByEmail(request.getEmail());
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        String code = tokenService.generateCode();
        tokenService.saveToken(user, code);
        sendForgotPasswordEmail(user, code);
    }

    public void resetPassword(ResetPasswordRequest request) {
        if (request.getEmail() == null || request.getEmail().isBlank()) {
            throw new RuntimeException("Email is required");
        }
        if (request.getCode() == null || request.getCode().isBlank()) {
            throw new RuntimeException("Verification code is required");
        }
        if (request.getNewPassword() == null || request.getConfirmPassword() == null) {
            throw new RuntimeException("New password and confirmation are required");
        }
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new RuntimeException("Passwords do not match");
        }
        if (!tokenService.validateCode(request.getCode())) {
            throw new RuntimeException("Invalid or expired verification code");
        }

        Utilisateur user = tokenService.getUtilisateurByCode(request.getCode());
        if (user == null) {
            throw new RuntimeException("Invalid verification code");
        }
        if (!user.getEmail().equalsIgnoreCase(request.getEmail())) {
            throw new RuntimeException("Email does not match verification code");
        }

        String encodedNewPassword = passwordService.encodePassword(request.getNewPassword());
        user.setMotDePasse(encodedNewPassword);
        saveUser(user);
        tokenService.deleteCode(request.getToken());
    }

    private void sendForgotPasswordEmail(Utilisateur user, String code) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, "UTF-8");
            String fromAddress = mailFrom != null && !mailFrom.isBlank() ? mailFrom : "no-reply@smartrecrute.com";
            helper.setFrom(fromAddress);
            helper.setTo(user.getEmail());
            helper.setSubject("SmartRecrute password reset code");
            String body = "Hello " + user.getNom() + ",\n\n" +
                    "Your password reset code is: " + code + "\n\n" +
                    "Enter this code on the reset password page. The code expires in 15 minutes.\n\n" +
                    "If you did not request this, please ignore this email.\n\n" +
                    "SmartRecrute Team";
            helper.setText(body, false);
            javaMailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send reset email: " + e.getMessage());
        }
    }

    public Utilisateur findUserByEmail(String email) {
        // Check each user type
        Administrateur admin = administrateurService.findByEmail(email);
        if (admin != null) return admin;

        Candidat candidat = candidatService.findByEmail(email);
        if (candidat != null) return candidat;

        Recruteur recruteur = recruteurService.findByEmail(email);
        if (recruteur != null) return recruteur;

        return null;
    }

    private void saveUser(Utilisateur user) {
        if (user instanceof Administrateur) {
            administrateurService.create((Administrateur) user);
        } else if (user instanceof Candidat) {
            candidatService.create((Candidat) user);
        } else if (user instanceof Recruteur) {
            recruteurService.create((Recruteur) user);
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Utilisateur user = findUserByEmail(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found with email: " + username);
        }
        return user;
    }
}