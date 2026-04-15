package com.smartrecrute.smartrecrute;

import com.smartrecrute.smartrecrute.entity.Tag;
import com.smartrecrute.smartrecrute.service.TagService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SmartrecruteApplication {

	public static void main(String[] args) {
		SpringApplication.run(SmartrecruteApplication.class, args);
	}

	@Bean
	CommandLineRunner initDatabase(TagService tagService) {
		return args -> {
			// Seed essential TECH skills (most used)
			String[] techTags = {"Java", "Spring Boot", "Angular", "React", "Python", "JavaScript", "TypeScript", "Node.js", "PostgreSQL", "MongoDB", "Docker", "Kubernetes", "AWS", "Git", "REST API", "CI/CD"};
			
			// Seed popular FRAMEWORK skills
			String[] frameTags = {"Spring Security", "NestJS", "Next.js", "Redux", "Jest", "Cypress"};
			
			// Seed essential TOOLS
			String[] toolTags = {"VS Code", "IntelliJ IDEA", "Postman", "Docker Desktop", "Maven", "npm"};
			
			// Seed important SOFT skills
			String[] softTags = {"Leadership", "Communication", "Teamwork", "Problem Solving", "Agile", "Scrum"};
			
			// Seed common languages
			String[] langTags = {"English", "French", "Spanish", "German"};
			
			createTagsIfNotExists(tagService, techTags, "TECH");
			createTagsIfNotExists(tagService, frameTags, "FRAME");
			createTagsIfNotExists(tagService, toolTags, "TOOL");
			createTagsIfNotExists(tagService, softTags, "SOFT");
			createTagsIfNotExists(tagService, langTags, "LANG");
			
			int total = techTags.length + frameTags.length + toolTags.length + softTags.length + langTags.length;
			System.out.println("✅ Seeded " + total + " skills in 5 categories!");
		};
	}
	
	private void createTagsIfNotExists(TagService tagService, String[] tags, String category) {
		for (String tagName : tags) {
			try {
				// Check if tag already exists
				var existingTags = tagService.getAll();
				boolean exists = existingTags.stream()
					.anyMatch(t -> t.getLibelle().equals(tagName) && t.getCategorie().equals(category));
				
				if (!exists) {
					Tag tag = new Tag();
					tag.setLibelle(tagName);
					tag.setCategorie(category);
					tagService.create(tag);
				}
			} catch (Exception e) {
				// Skip on error
			}
		}
	}
}
