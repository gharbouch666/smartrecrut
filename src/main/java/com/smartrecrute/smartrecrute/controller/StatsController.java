package com.smartrecrute.smartrecrute.controller;

import com.smartrecrute.smartrecrute.repository.*;
import com.smartrecrute.smartrecrute.jwt.JwtRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/stats")
public class StatsController {

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

    @Autowired
    private JwtRepository jwtRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();
        
        stats.put("totalCandidates", candidatRepository.count());
        stats.put("totalRecruiters", recruteurRepository.count());
        stats.put("totalOffres", offreRepository.count());
        stats.put("totalCandidatures", candidatureRepository.count());
        stats.put("totalTags", tagRepository.count());

        return ResponseEntity.ok(stats);
    }

    @GetMapping("/recruitment")
    public ResponseEntity<Map<String, Object>> getRecruitmentStats() {
        Map<String, Object> stats = new HashMap<>();
        
        stats.put("averageTimeToHire", 15);
        stats.put("interviewToHireRatio", 0.4);
        stats.put("applicationsPerJob", 12);

        return ResponseEntity.ok(stats);
    }

    @GetMapping("/tags/popular")
    public ResponseEntity<Map<String, Integer>> getPopularTags() {
        List<Object[]> tagCounts = profilTagRepository.countTagsByUsage();
        Map<String, Integer> tags = new HashMap<>();
        for (Object[] row : tagCounts) {
            tags.put((String) row[0], ((Long) row[1]).intValue());
        }
        return ResponseEntity.ok(tags);
    }

    @GetMapping("/users")
    public ResponseEntity<Map<String, Object>> getAllUsers() {
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
        return ResponseEntity.ok(users);
    }

@RequestMapping(value = "/users", method = RequestMethod.DELETE)
    @Transactional
    public ResponseEntity<Void> deleteUser(
            @RequestParam("id") Long id, 
            @RequestParam("type") String type) {
        try {
            System.out.println("DELETE for id=" + id + " type=" + type);
            
            jwtRepository.deleteByUserId(id);
            
            switch (type.toLowerCase()) {
                case "candidat":
                    System.out.println("Deleting candidat " + id);
                    candidatRepository.deleteById(id);
                    break;
                case "recruteur":
                    System.out.println("Deleting recruteur " + id);
                    recruteurRepository.deleteById(id);
                    break;
                case "administrateur":
                    System.out.println("Deleting administrateur " + id);
                    administrateurRepository.deleteById(id);
                    break;
                default:
                    System.out.println("Unknown type: " + type);
                    return ResponseEntity.notFound().build();
            }
            System.out.println("Delete success");
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.notFound().build();
        }
    }
    
// NATIVE SQL DELETE - bypasses all ORM constraints
    @GetMapping("/users/delete/{id}/{type}")
    @Transactional  
    public ResponseEntity<Void> deleteUserNative(@PathVariable Long id, @PathVariable String type) {
        try {
            System.out.println("NATIVE DELETE: id=" + id + " type=" + type);
            String lowerType = type.toLowerCase();
            
            // Delete JWT first
            try { jwtRepository.deleteByUserId(id); } catch (Exception e) {}
            
            // Delete messages user sent
            entityManager.createNativeQuery("DELETE FROM message WHERE sender_id = ?1").setParameter(1, id).executeUpdate();
            // Delete messages user received  
            entityManager.createNativeQuery("DELETE FROM message WHERE receiver_id = ?1").setParameter(1, id).executeUpdate();
            
            if (lowerType.equals("candidat")) {
                // Delete profil tags
                entityManager.createNativeQuery("DELETE FROM profil_tag WHERE candidat_id = ?1").setParameter(1, id).executeUpdate();
                // Delete candidatures  
                entityManager.createNativeQuery("DELETE FROM candidature WHERE candidat_id = ?1").setParameter(1, id).executeUpdate();
                // Delete subtype table FIRST, then utilisateur
                entityManager.createNativeQuery("DELETE FROM candidat WHERE id = ?1").setParameter(1, id).executeUpdate();
                entityManager.createNativeQuery("DELETE FROM utilisateur WHERE id = ?1").setParameter(1, id).executeUpdate();
                System.out.println("Candidat " + id + " deleted via SQL");
                
            } else if (lowerType.equals("recruteur")) {
                // Get jobs first
                List<?> jobs = entityManager.createNativeQuery("SELECT id FROM offre WHERE recruteur_id = ?1").setParameter(1, id).getResultList();
                for (Object jobId : jobs) {
                    Long jid = ((Number) jobId).longValue();
                    entityManager.createNativeQuery("DELETE FROM tag_offre WHERE offre_id = ?1").setParameter(1, jid).executeUpdate();
                    entityManager.createNativeQuery("DELETE FROM candidature WHERE offre_id = ?1").setParameter(1, jid).executeUpdate();
                }
                entityManager.createNativeQuery("DELETE FROM offre WHERE recruteur_id = ?1").setParameter(1, id).executeUpdate();
                // Delete subtype table FIRST, then utilisateur
                entityManager.createNativeQuery("DELETE FROM recruteur WHERE id = ?1").setParameter(1, id).executeUpdate();
                entityManager.createNativeQuery("DELETE FROM utilisateur WHERE id = ?1").setParameter(1, id).executeUpdate();
                System.out.println("Recruteur " + id + " deleted via SQL");
                
            } else if (lowerType.equals("administrateur")) {
                // Delete subtype table FIRST, then utilisateur
                entityManager.createNativeQuery("DELETE FROM administrateur WHERE id = ?1").setParameter(1, id).executeUpdate();
                entityManager.createNativeQuery("DELETE FROM utilisateur WHERE id = ?1").setParameter(1, id).executeUpdate();
                System.out.println("Admin " + id + " deleted via SQL");
            }
            
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            System.out.println("SQL DELETE ERROR: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/users/update/{id}/{type}")
    @Transactional
    public ResponseEntity<Void> updateUser(@PathVariable Long id, @PathVariable String type, 
            @RequestBody Map<String, String> data) {
        try {
            String newNom = data.get("nom");
            String newEmail = data.get("email");
            String newMotDePasse = data.get("motDePasse");
            
            switch (type.toLowerCase()) {
                case "candidat":
                    var candidat = candidatRepository.findById(id);
                    if (candidat.isPresent()) {
                        var c = candidat.get();
                        c.setNom(newNom);
                        c.setEmail(newEmail);
                        if (newMotDePasse != null && !newMotDePasse.isEmpty()) {
                            c.setMotDePasse(newMotDePasse);
                        }
                        candidatRepository.save(c);
                    }
                    break;
                case "recruteur":
                    var recruteur = recruteurRepository.findById(id);
                    if (recruteur.isPresent()) {
                        var r = recruteur.get();
                        r.setNom(newNom);
                        r.setEmail(newEmail);
                        if (newMotDePasse != null && !newMotDePasse.isEmpty()) {
                            r.setMotDePasse(newMotDePasse);
                        }
                        recruteurRepository.save(r);
                    }
                    break;
                case "administrateur":
                    var admin = administrateurRepository.findById(id);
                    if (admin.isPresent()) {
                        var a = admin.get();
                        a.setNom(newNom);
                        a.setEmail(newEmail);
                        if (newMotDePasse != null && !newMotDePasse.isEmpty()) {
                            a.setMotDePasse(newMotDePasse);
                        }
                        administrateurRepository.save(a);
                    }
                    break;
                default:
                    return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
}