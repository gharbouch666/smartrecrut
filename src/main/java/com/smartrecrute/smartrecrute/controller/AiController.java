package com.smartrecrute.smartrecrute.controller;

import com.smartrecrute.smartrecrute.entity.Candidature;
import com.smartrecrute.smartrecrute.service.CandidatureService;
import com.smartrecrute.smartrecrute.service.MatchingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/ai")
public class AiController {

    @Autowired
    private CandidatureService candidatureService;

    @Autowired
    private MatchingService matchingService;

    @Value("${openai.api.key:}")
    private String openAiApiKey;

    @PostMapping("/generate-description")
    public ResponseEntity<Map<String, String>> generateDescription(@RequestBody Map<String, String> request) {
        String title = request.get("titre");
        String description = "We are looking for a " + title + " to join our team. " +
            "You will be responsible for " + title + " duties including technical implementation, " +
            "collaboration with cross-functional teams, and contribution to our innovative projects.";

        Map<String, String> response = new HashMap<>();
        response.put("description", description);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/search-candidates")
    public ResponseEntity<Map<String, String>> searchCandidates(@RequestBody Map<String, String> request) {
        String query = request.get("query");
        String response = "Search results for: " + query + "\\n\\n" +
            "This feature requires OpenAI API key configuration.";

        Map<String, String> result = new HashMap<>();
        result.put("results", response);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/interview-questions")
    public ResponseEntity<Map<String, Object>> generateInterviewQuestions(@RequestBody Map<String, Object> request) {
        List<String> questions = Arrays.asList(
            "Can you describe your experience with " + request.get("skill") + "?",
            "What projects have you built using " + request.get("skill") + "?",
            "How do you handle " + request.get("skill") + " performance issues?",
            "Explain your approach to testing " + request.get("skill") + " applications.",
            "What are the best practices you've followed in " + request.get("skill") + "?"
        );

        Map<String, Object> response = new HashMap<>();
        response.put("questions", questions);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/explain-score")
    public ResponseEntity<Map<String, String>> explainScore(@RequestBody Map<String, Long> request) {
        Long candidatureId = request.get("candidatureId");
        Candidature candidature = candidatureService.getById(candidatureId);

        String explanation = "Score breakdown for this candidate:\\n";
        explanation += "- Base skills match: " + (candidature.getScoreTotal() != null ? candidature.getScoreTotal() * 0.6 : 0) + "%\\n";
        explanation += "- Experience level: " + (candidature.getScoreTotal() != null ? candidature.getScoreTotal() * 0.25 : 0) + "%\\n";
        explanation += "- Additional skills: " + (candidature.getScoreTotal() != null ? candidature.getScoreTotal() * 0.15 : 0) + "%";

        Map<String, String> result = new HashMap<>();
        result.put("explanation", explanation);
        return ResponseEntity.ok(result);
    }
}