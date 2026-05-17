package com.smartrecrute.smartrecrute.service;

import com.smartrecrute.smartrecrute.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class StatsService {

    @Autowired
    private CandidatRepository candidatRepository;

    @Autowired
    private RecruteurRepository recruteurRepository;

    @Autowired
    private AdministrateurRepository administrateurRepository;

    @Autowired
    private OffreRepository offreRepository;

    @Autowired
    private CandidatureRepository candidatureRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private ProfilTagRepository profilTagRepository;

    @Autowired
    private TagOffreRepository tagOffreRepository;

    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();

        stats.put("totalCandidates", candidatRepository.count());
        stats.put("totalRecruiters", recruteurRepository.count());
        stats.put("totalOffres", offreRepository.count());
        stats.put("totalCandidatures", candidatureRepository.count());
        stats.put("totalTags", tagRepository.count());

        return stats;
    }

    public Map<String, Object> getRecruitmentStats() {
        Map<String, Object> stats = new HashMap<>();

        // These would ideally be calculated from actual data
        stats.put("averageTimeToHire", 15);
        stats.put("interviewToHireRatio", 0.4);
        stats.put("applicationsPerJob", 12);

        return stats;
    }

    public Map<String, Integer> getPopularTags() {
        List<Object[]> tagCounts = profilTagRepository.countTagsByUsage();
        Map<String, Integer> tags = new HashMap<>();
        for (Object[] row : tagCounts) {
            tags.put((String) row[0], ((Long) row[1]).intValue());
        }
        return tags;
    }

    public Map<String, Object> getAllUsers() {
        Map<String, Object> users = new HashMap<>();
        users.put("candidats", candidatRepository.findAll().stream()
                .map(c -> Map.of("id", c.getId(), "nom", c.getNom(), "email", c.getEmail()))
                .collect(Collectors.toList()));
        users.put("recruteurs", recruteurRepository.findAll().stream()
                .map(r -> Map.of("id", r.getId(), "nom", r.getNom(), "email", r.getEmail()))
                .collect(Collectors.toList()));
        users.put("administrateurs", administrateurRepository.findAll().stream()
                .map(a -> Map.of("id", a.getId(), "nom", a.getNom(), "email", a.getEmail()))
                .collect(Collectors.toList()));
        return users;
    }
}