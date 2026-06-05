package com.smartrecrute.smartrecrute.service;

import com.smartrecrute.smartrecrute.entity.*;
import com.smartrecrute.smartrecrute.enums.NiveauExpertise;
import com.smartrecrute.smartrecrute.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class MatchingService {

    @Autowired
    private CandidatRepository candidatRepository;

    @Autowired
    private OffreRepository offreRepository;

    @Autowired
    private CandidatureRepository candidatureRepository;

    @Autowired
    private TagOffreRepository tagOffreRepository;

    @Autowired
    private ProfilTagRepository profilTagRepository;

    public Double calculateScore(Long candidatId, Long offreId) {
        Candidat candidat = candidatRepository.findById(candidatId)
            .orElseThrow(() -> new RuntimeException("Candidat not found"));
        Offre offre = offreRepository.findById(offreId)
            .orElseThrow(() -> new RuntimeException("Offre not found"));

        return calculateScore(candidat, offre);
    }

    public Double calculateScore(Candidat candidat, Offre offre) {
        List<TagOffre> jobTags = tagOffreRepository.findByOffre(offre);
        List<ProfilTag> candidateSkills = profilTagRepository.findByCandidat(candidat);

        if (jobTags.isEmpty()) {
            return 50.0;
        }

        double totalWeight = 0.0;
        double earnedWeight = 0.0;

        for (TagOffre jobTag : jobTags) {
            double weight = jobTag.getPoids() != null ? jobTag.getPoids() : 1.0;
            totalWeight += weight;

            Optional<ProfilTag> matchingSkill = candidateSkills.stream()
                .filter(cs -> cs.getTag().getId().equals(jobTag.getTag().getId()))
                .findFirst();

            if (matchingSkill.isPresent()) {
                double levelScore = getLevelScore(matchingSkill.get().getNiveau());
                earnedWeight += weight * levelScore;
            }
        }

        // Check if any mandatory tag is missing - if so, score is 0%
        for (TagOffre jobTag : jobTags) {
            if (jobTag.getObligatoire() != null && jobTag.getObligatoire()) {
                boolean hasMatchingSkill = candidateSkills.stream()
                    .anyMatch(cs -> cs.getTag().getId().equals(jobTag.getTag().getId()));
                if (!hasMatchingSkill) {
                    return 0.0;
                }
            }
        }

        double rawScore = totalWeight > 0 ? (earnedWeight / totalWeight) * 100 : 50;
        return Math.max(0.0, Math.min(100.0, rawScore));
    }

    private double getLevelScore(NiveauExpertise niveau) {
        if (niveau == null) return 0.6;
        switch (niveau) {
            case EXPERT: return 1.0;
            case INTERMEDIAIRE: return 0.8;
            case DEBUTANT: return 0.5;
            default: return 0.6;
        }
    }

    public List<Candidature> rankCandidatesByOffre(Long offreId) {
        Offre offre = offreRepository.findById(offreId)
            .orElseThrow(() -> new RuntimeException("Offre not found"));

        List<Candidature> candidatures = candidatureRepository.findByOffre(offre);

        for (Candidature cand : candidatures) {
            double score = calculateScore(cand.getCandidat(), offre);
            cand.setScoreTotal(score);
            candidatureRepository.save(cand);
        }

        candidatures.sort((a, b) -> {
            double diff = (b.getScoreTotal() != null ? b.getScoreTotal() : 0) - 
                      (a.getScoreTotal() != null ? a.getScoreTotal() : 0);
            return diff > 0 ? 1 : diff < 0 ? -1 : 0;
        });

        return candidatures;
    }

    public void recalculateAllScoresForOffre(Long offresId) {
        List<Candidature> candidatures = candidatureRepository.findByOffreId(offresId);
        Offre offre = offreRepository.findById(offresId)
            .orElseThrow(() -> new RuntimeException("Offre not found"));

        for (Candidature cand : candidatures) {
            double score = calculateScore(cand.getCandidat(), offre);
            cand.setScoreTotal(score);
            candidatureRepository.save(cand);
        }
    }

    public Map<String, Object> getScoreBreakdown(Long candidatId, Long offreId) {
        Candidat candidat = candidatRepository.findById(candidatId)
            .orElseThrow(() -> new RuntimeException("Candidat not found"));
        Offre offre = offreRepository.findById(offreId)
            .orElseThrow(() -> new RuntimeException("Offre not found"));

        List<TagOffre> jobTags = tagOffreRepository.findByOffre(offre);
        List<ProfilTag> candidateSkills = profilTagRepository.findByCandidat(candidat);

        List<String> matchedSkills = new ArrayList<>();
        List<String> missingMandatorySkills = new ArrayList<>();
        List<String> missingBonusSkills = new ArrayList<>();
        List<String> extraSkills = new ArrayList<>();
        
        double mandatoryWeight = 0.0;
        double mandatoryEarnedWeight = 0.0;
        double bonusWeight = 0.0;
        double bonusEarnedWeight = 0.0;

        for (TagOffre jobTag : jobTags) {
            String skillName = jobTag.getTag().getLibelle();
            double weight = jobTag.getPoids() != null ? jobTag.getPoids() : 1.0;
            Optional<ProfilTag> matchingSkill = candidateSkills.stream()
                .filter(cs -> cs.getTag().getId().equals(jobTag.getTag().getId()))
                .findFirst();

            if (jobTag.getObligatoire() != null && jobTag.getObligatoire()) {
                mandatoryWeight += weight;
                if (matchingSkill.isPresent()) {
                    matchedSkills.add(skillName);
                    double levelScore = getLevelScore(matchingSkill.get().getNiveau());
                    mandatoryEarnedWeight += weight * levelScore;
                } else {
                    missingMandatorySkills.add(skillName);
                }
            } else {
                bonusWeight += weight;
                if (matchingSkill.isPresent()) {
                    matchedSkills.add(skillName);
                    double levelScore = getLevelScore(matchingSkill.get().getNiveau());
                    bonusEarnedWeight += weight * levelScore;
                } else {
                    missingBonusSkills.add(skillName);
                }
            }
        }

        for (ProfilTag candidateSkill : candidateSkills) {
            boolean found = jobTags.stream()
                .anyMatch(jt -> jt.getTag().getId().equals(candidateSkill.getTag().getId()));
            if (!found) {
                extraSkills.add(candidateSkill.getTag().getLibelle());
            }
        }

        double mandatoryScore = mandatoryWeight > 0 ? (mandatoryEarnedWeight / mandatoryWeight) * 100 : 0.0;
        double bonusScore = bonusWeight > 0 ? (bonusEarnedWeight / bonusWeight) * 100 : 0.0;
        
        Map<String, Object> breakdown = new HashMap<>();
        breakdown.put("score", calculateScore(candidat, offre));
        breakdown.put("mandatoryScore", mandatoryScore);
        breakdown.put("bonusScore", bonusScore);
        breakdown.put("matchedSkills", matchedSkills);
        breakdown.put("missingRequiredSkills", missingMandatorySkills);
        breakdown.put("requiredSkillsMissing", missingMandatorySkills.size() > 0);
        breakdown.put("missingBonusSkills", missingBonusSkills);
        breakdown.put("extraSkills", extraSkills);
        return breakdown;
    }
}