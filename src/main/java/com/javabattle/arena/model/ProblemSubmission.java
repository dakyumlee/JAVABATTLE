package com.javabattle.arena.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "problem_submissions")
public class ProblemSubmission {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(name = "problem_title", length = 500)
    private String problemTitle;
    
    @Column(columnDefinition = "TEXT")
    private String answer;
    
    @Column(name = "submitted_at", nullable = false)
    private LocalDateTime submittedAt;
    
    private Integer score;
    
    @Column(columnDefinition = "TEXT")
    private String feedback;
    
    public ProblemSubmission() {
        this.submittedAt = LocalDateTime.now();
    }
    
    public ProblemSubmission(Long userId, String problemTitle, String answer) {
        this.userId = userId;
        this.problemTitle = problemTitle;
        this.answer = answer;
        this.submittedAt = LocalDateTime.now();
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public String getProblemTitle() {
        return problemTitle;
    }
    
    public void setProblemTitle(String problemTitle) {
        this.problemTitle = problemTitle;
    }
    
    public String getAnswer() {
        return answer;
    }
    
    public void setAnswer(String answer) {
        this.answer = answer;
    }
    
    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }
    
    public void setSubmittedAt(LocalDateTime submittedAt) {
        this.submittedAt = submittedAt;
    }
    
    public Integer getScore() {
        return score;
    }
    
    public void setScore(Integer score) {
        this.score = score;
    }
    
    public String getFeedback() {
        return feedback;
    }
    
    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }
}