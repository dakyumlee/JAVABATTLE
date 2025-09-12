package com.javabattle.arena.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "problems")
public class Problem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long problemId;

    @Column(name = "TITLE", nullable = false, length = 200)
    private String title;

    @Column(name = "DESCRIPTION", nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(name = "DIFFICULTY", length = 20)
    private String difficulty = "EASY";

    @Column(name = "CATEGORY", length = 50)
    private String category;

    @Column(name = "SAMPLE_INPUT", columnDefinition = "TEXT")
    private String sampleInput;

    @Column(name = "SAMPLE_OUTPUT", columnDefinition = "TEXT")
    private String sampleOutput;

    @Column(name = "SOLUTION_TEMPLATE", columnDefinition = "TEXT")
    private String solutionTemplate;

    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt;

    @Column(name = "expected_concepts", columnDefinition = "TEXT")
    private String expectedConcepts;

    @Column(name = "evaluation_type", length = 20)
    private String evaluationType = "AI_ASSISTED";

    public Problem() {
        this.createdAt = LocalDateTime.now();
    }

    public Long getProblemId() { return problemId; }
    public void setProblemId(Long problemId) { this.problemId = problemId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getSampleInput() { return sampleInput; }
    public void setSampleInput(String sampleInput) { this.sampleInput = sampleInput; }

    public String getSampleOutput() { return sampleOutput; }
    public void setSampleOutput(String sampleOutput) { this.sampleOutput = sampleOutput; }

    public String getSolutionTemplate() { return solutionTemplate; }
    public void setSolutionTemplate(String solutionTemplate) { this.solutionTemplate = solutionTemplate; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public String getExpectedConcepts() { return expectedConcepts; }
    public void setExpectedConcepts(String expectedConcepts) { this.expectedConcepts = expectedConcepts; }

    public String getEvaluationType() { return evaluationType; }
    public void setEvaluationType(String evaluationType) { this.evaluationType = evaluationType; }
}