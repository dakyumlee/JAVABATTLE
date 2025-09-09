package com.javabattle.arena.model;

public class Problem {
    private Long id;
    private String title;
    private String description;
    private String difficulty;
    private String expectedOutput;
    private String testInput;
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }
    
    public String getExpectedOutput() { return expectedOutput; }
    public void setExpectedOutput(String expectedOutput) { this.expectedOutput = expectedOutput; }
    
    public String getTestInput() { return testInput; }
    public void setTestInput(String testInput) { this.testInput = testInput; }
}
