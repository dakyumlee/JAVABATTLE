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

    @Column(name = "problem_id")
    private Long problemId;

    @Column(name = "problem_title", length = 500)
    private String problemTitle;

    @Column(columnDefinition = "TEXT")
    private String answer;

    @Column(name = "submitted_at", nullable = false)
    private LocalDateTime submittedAt;

    private Integer score;

    @Column(columnDefinition = "TEXT")
    private String feedback;

    @Column(name = "is_correct")
    private Boolean isCorrect;

    @Column(name = "ai_feedback", columnDefinition = "TEXT")
    private String aiFeedback;

    @Column(name = "is_creative_solution")
    private Boolean isCreativeSolution = false;

    @Column(name = "evaluation_time_ms")
    private Long evaluationTimeMs;

    public ProblemSubmission() {
        this.submittedAt = LocalDateTime.now();
    }

    public ProblemSubmission(Long userId, String problemTitle, String answer) {
        this.userId = userId;
        this.problemTitle = problemTitle;
        this.answer = answer;
        this.submittedAt = LocalDateTime.now();
    }

    public static ProblemSubmissionBuilder builder() {
        return new ProblemSubmissionBuilder();
    }

    public static class ProblemSubmissionBuilder {
        private ProblemSubmission submission = new ProblemSubmission();

        public ProblemSubmissionBuilder userId(String userId) {
            submission.userId = Long.valueOf(userId);
            return this;
        }

        public ProblemSubmissionBuilder problemId(Long problemId) {
            submission.problemId = problemId;
            return this;
        }

        public ProblemSubmissionBuilder answer(String answer) {
            submission.answer = answer;
            return this;
        }

        public ProblemSubmissionBuilder isCorrect(Boolean isCorrect) {
            submission.isCorrect = isCorrect;
            return this;
        }

        public ProblemSubmissionBuilder score(Integer score) {
            submission.score = score;
            return this;
        }

        public ProblemSubmissionBuilder aiFeedback(String aiFeedback) {
            submission.aiFeedback = aiFeedback;
            return this;
        }

        public ProblemSubmissionBuilder isCreativeSolution(Boolean isCreativeSolution) {
            submission.isCreativeSolution = isCreativeSolution;
            return this;
        }

        public ProblemSubmissionBuilder submittedAt(LocalDateTime submittedAt) {
            submission.submittedAt = submittedAt;
            return this;
        }

        public ProblemSubmissionBuilder evaluationTimeMs(Long evaluationTimeMs) {
            submission.evaluationTimeMs = evaluationTimeMs;
            return this;
        }

        public ProblemSubmission build() {
            return submission;
        }
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

    public Long getProblemId() {
        return problemId;
    }

    public void setProblemId(Long problemId) {
        this.problemId = problemId;
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

    public Boolean getIsCorrect() {
        return isCorrect;
    }

    public void setIsCorrect(Boolean isCorrect) {
        this.isCorrect = isCorrect;
    }

    public String getAiFeedback() {
        return aiFeedback;
    }

    public void setAiFeedback(String aiFeedback) {
        this.aiFeedback = aiFeedback;
    }

    public Boolean getIsCreativeSolution() {
        return isCreativeSolution;
    }

    public void setIsCreativeSolution(Boolean isCreativeSolution) {
        this.isCreativeSolution = isCreativeSolution;
    }

    public Long getEvaluationTimeMs() {
        return evaluationTimeMs;
    }

    public void setEvaluationTimeMs(Long evaluationTimeMs) {
        this.evaluationTimeMs = evaluationTimeMs;
    }
}