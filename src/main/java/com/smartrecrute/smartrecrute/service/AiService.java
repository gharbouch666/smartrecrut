package com.smartrecrute.smartrecrute.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class AiService {

    @Value("${ollama.host:http://localhost:11434}")
    private String ollamaHost;

    @Value("${ollama.model:llama3.2}")
    private String ollamaModel;

    private final RestTemplate restTemplate = new RestTemplate();

    public String generateJobDescription(String title, String description) {
        String prompt = "Generate a professional job description for the position: " + title +
                ". Description: " + (description != null ? description : "") +
                ". Make it detailed and engaging, suitable for a job posting.";
        
        String response = callOllama(prompt);
        return response != null && !response.isEmpty() ? response : 
                generateMockJobDescription(title, description);
    }

    public String searchCandidates(String query) {
        String prompt = "Based on the search query: '" + query + "', provide relevant candidate search insights and recommendations for recruiters.";
        String response = callOllama(prompt);
        return response != null && !response.isEmpty() ? response : 
                "Search results for: \"" + query + "\"\n\n" +
                "This feature requires AI API key configuration for actual semantic search.\n" +
                "Currently showing mock results based on keyword matching.";
    }

    public String[] generateInterviewQuestions(String skill, String jobTitle) {
        if (skill == null || skill.isEmpty()) {
            skill = "the required skills";
        }
        if (jobTitle == null || jobTitle.isEmpty()) {
            jobTitle = "this position";
        }
        
        String prompt = "Generate 8-10 professional interview questions for a " + jobTitle + " position focusing on the skill: " + skill +
                ". Make the questions practical, experience-based, and suitable for assessing candidate proficiency.";
        
        String response = callOllama(prompt);
        if (response != null && !response.isEmpty()) {
            // Parse response into array of questions
            List<String> questions = new ArrayList<>();
            String[] lines = response.split("\\n");
            for (String line : lines) {
                String trimmed = line.trim();
                if (!trimmed.isEmpty() && (trimmed.startsWith("-") || trimmed.startsWith("*") || 
                        Character.isDigit(trimmed.charAt(0)))) {
                    // Remove leading dash, bullet, or number
                    int startIdx = 0;
                    if (trimmed.startsWith("-") || trimmed.startsWith("*")) {
                        startIdx = 1;
                    } else if (Character.isDigit(trimmed.charAt(0))) {
                        int dotIndex = trimmed.indexOf('.');
                        if (dotIndex > 0) {
                            startIdx = dotIndex + 1;
                        }
                    }
                    String question = trimmed.substring(startIdx).trim();
                    if (!question.isEmpty()) {
                        questions.add(question);
                    }
                } else if (!trimmed.isEmpty() && trimmed.length() > 10) {
                    // Treat as a question if it's substantial
                    questions.add(trimmed);
                }
            }
            
            // If we got questions from parsing, use them; otherwise fallback
            if (!questions.isEmpty()) {
                return questions.toArray(new String[0]);
            }
        }
        
        // Fallback to mock responses
        return generateMockInterviewQuestions(skill, jobTitle);
    }

    public String explainScore(Double totalScore, Double obligatoryScore, Double bonusScore, 
                           List<String> matchedSkills, List<String> missingSkills, List<String> extraSkills) {
        if (totalScore == null) {
            return "No score available for explanation.";
        }
        
        StringBuilder promptBuilder = new StringBuilder();
        promptBuilder.append("Explain this candidate job match score in a professional, encouraging way:\n\n");
        promptBuilder.append("Score Details:\n");
        promptBuilder.append("- Overall Match Score: ").append(String.format("%.1f", totalScore)).append("%\n");
        promptBuilder.append("- Mandatory Skills Score: ").append(obligatoryScore != null ? String.format("%.1f", obligatoryScore) : "0.0").append("%\n");
        promptBuilder.append("- Bonus Skills Score: ").append(bonusScore != null ? String.format("%.1f", bonusScore) : "0.0").append("%\n");
        
        if (matchedSkills != null && !matchedSkills.isEmpty()) {
            promptBuilder.append("\nMatched Skills: ").append(String.join(", ", matchedSkills)).append("\n");
        }
        if (missingSkills != null && !missingSkills.isEmpty()) {
            promptBuilder.append("\nMissing Required Skills: ").append(String.join(", ", missingSkills)).append("\n");
        }
        if (extraSkills != null && !extraSkills.isEmpty()) {
            promptBuilder.append("\nExtra Skills (not required): ").append(String.join(", ", extraSkills)).append("\n");
        }
        
        promptBuilder.append("\nProvide a clear, helpful interpretation focusing on strengths and areas for improvement. Be professional and constructive.");
        
        String response = callOllama(promptBuilder.toString());
        return response != null && !response.isEmpty() ? response : 
                generateMockExplainScore(totalScore, obligatoryScore, bonusScore, matchedSkills, missingSkills, extraSkills);
    }

    private String callOllama(String prompt) {
        try {
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", ollamaModel);
            requestBody.put("prompt", prompt);
            requestBody.put("stream", false);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
            
            ResponseEntity<Map> response = restTemplate.exchange(
                    ollamaHost + "/api/generate",
                    HttpMethod.POST,
                    request,
                    Map.class
            );
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return (String) response.getBody().get("response");
            }
        } catch (Exception e) {
            // Log the error in production - for now just return null to trigger fallback
            // System.err.println("Ollama API call failed: " + e.getMessage());
        }
        return null;
    }

    // Mock fallback methods
    private String generateMockJobDescription(String title, String description) {
        if (title == null || title.isEmpty()) {
            return "We are looking for a talented professional to join our team.";
        }
        return "We are looking for a " + title + " to join our team. " +
               "You will be responsible for " + title.toLowerCase() + " duties including " +
               "technical implementation, collaboration with cross-functional teams, " +
               "and contribution to our innovative projects." +
               (description != null && !description.isEmpty() ? " " + description : "");
    }

    private String[] generateMockInterviewQuestions(String skill, String jobTitle) {
        if (skill == null || skill.isEmpty()) {
            skill = "the required skills";
        }
        if (jobTitle == null || jobTitle.isEmpty()) {
            jobTitle = "this position";
        }
        
        return new String[]{
            "Can you describe your experience with " + skill + "?",
            "What projects have you built using " + skill + "?",
            "How do you handle " + skill + " related challenges in " + jobTitle + " roles?",
            "Explain your approach to learning new aspects of " + skill + ".",
            "What are the best practices you've followed when working with " + skill + "?",
            "Describe a time when you had to solve a difficult problem using " + skill + ".",
            "How do you stay updated with the latest developments in " + skill + "?",
            "Can you walk us through your workflow when using " + skill + "?"
        };
    }

    private String generateMockExplainScore(Double totalScore, Double obligatoryScore, Double bonusScore, 
                                           List<String> matchedSkills, List<String> missingSkills, List<String> extraSkills) {
        if (totalScore == null) {
            return "No score available for explanation.";
        }
        
        StringBuilder explanation = new StringBuilder();
        explanation.append("## Candidate Match Analysis\n\n");
        explanation.append("**Overall Match Score: ").append(String.format("%.0f", totalScore)).append("%**\n\n");
        
        if (matchedSkills != null && !matchedSkills.isEmpty()) {
            explanation.append("### ✓ Matched Skills\n");
            for (String skill : matchedSkills) {
                explanation.append("• ").append(skill).append("\n");
            }
            explanation.append("\n");
        }
        
        if (missingSkills != null && !missingSkills.isEmpty()) {
            explanation.append("### ⚠ Missing Required Skills\n");
            for (String skill : missingSkills) {
                explanation.append("• ").append(skill).append("\n");
            }
            explanation.append("\n");
        }
        
        if (extraSkills != null && !extraSkills.isEmpty()) {
            explanation.append("### ➕ Additional Skills\n");
            for (String skill : extraSkills) {
                explanation.append("• ").append(skill).append("\n");
            }
            explanation.append("\n");
        }
        
        explanation.append("### Assessment\n");
        if (totalScore >= 80) {
            explanation.append("This candidate shows an excellent fit for the position with strong alignment to the job requirements. ");
            if (missingSkills != null && !missingSkills.isEmpty()) {
                explanation.append("The few missing skills could potentially be developed on the job.");
            } else {
                explanation.append("All required skills are matched.");
            }
        } else if (totalScore >= 60) {
            explanation.append("This is a good match. The candidate meets most core requirements ");
            if (missingSkills != null && !missingSkills.isEmpty()) {
                explanation.append("but would benefit from upskilling in: ").append(String.join(", ", missingSkills)).append(".");
            }
        } else if (totalScore >= 40) {
            explanation.append("This is a moderate match. The candidate has foundational skills but significant gaps in required areas. ");
            if (missingSkills != null && !missingSkills.isEmpty()) {
                explanation.append("Focus areas for development include: ").append(String.join(", ", missingSkills)).append(".");
            }
        } else {
            explanation.append("This candidate has significant skill gaps for this position. ");
            if (missingSkills != null && !missingSkills.isEmpty()) {
                explanation.append("Critical missing skills include: ").append(String.join(", ", missingSkills)).append(".");
            }
        }
        
        return explanation.toString();
    }
}