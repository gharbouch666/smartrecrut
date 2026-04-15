package com.smartrecrute.smartrecrute.service;

import com.smartrecrute.smartrecrute.entity.Candidat;
import com.smartrecrute.smartrecrute.entity.Offre;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    public void sendRejectionEmail(Candidat candidat, Offre offre, Double score) {
        System.out.println("[EMAIL] Sending rejection email to: " + candidat.getEmail());
        System.out.println("[EMAIL] Subject: Your application for " + offre.getTitre());
        System.out.println("[EMAIL] Body: Dear " + candidat.getNom() + ", Your score was " + score + "%. We regret...");
    }

    public void sendApplicationReceived(Candidat candidat, Offre offre) {
        System.out.println("[EMAIL] Sending confirmation to: " + candidat.getEmail());
        System.out.println("[EMAIL] Subject: Application received for " + offre.getTitre());
        System.out.println("[EMAIL] Body: Dear " + candidat.getNom() + ", We have received your application...");
    }
}