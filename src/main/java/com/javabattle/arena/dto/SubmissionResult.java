package com.javabattle.arena.dto;

import java.util.List;

public class SubmissionResult {
    private boolean isCorrect;
    private int score;
    private String feedback;
    private List<String> strengths;
    private List<String> improvements;
    private boolean isCreative;
    private String error;
    
    public SubmissionResult() {}
    
    public static SubmissionResult error(String errorMessage) {
        SubmissionResult result = new SubmissionResult();
        result.error = errorMessage;
        result.isCorrect = false;
        result.score = 0;
        return result;
    }
    
    public static SubmissionResultBuilder builder() {
        return new SubmissionResultBuilder();
    }
    
    public static class SubmissionResultBuilder {
        private SubmissionResult result = new SubmissionResult();
        
        public SubmissionResultBuilder isCorrect(boolean isCorrect) {
            result.isCorrect = isCorrect;
            return this;
        }
        
        public SubmissionResultBuilder score(int score) {
            result.score = score;
            return this;
        }
        
        public SubmissionResultBuilder feedback(String feedback) {
            result.feedback = feedback;
            return this;
        }
        
        public SubmissionResultBuilder strengths(List<String> strengths) {
            result.strengths = strengths;
            return this;
        }
        
        public SubmissionResultBuilder improvements(List<String> improvements) {
            result.improvements = improvements;
            return this;
        }
        
        public SubmissionResultBuilder isCreative(boolean isCreative) {
            result.isCreative = isCreative;
            return this;
        }
        
        public SubmissionResult build() {
            return result;
        }
    }
    
    public boolean isCorrect() {
        return isCorrect;
    }
    
    public void setCorrect(boolean correct) {
        isCorrect = correct;
    }
    
    public int getScore() {
        return score;
    }
    
    public void setScore(int score) {
        this.score = score;
    }
    
    public String getFeedback() {
        return feedback;
    }
    
    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }
    
    public List<String> getStrengths() {
        return strengths;
    }
    
    public void setStrengths(List<String> strengths) {
        this.strengths = strengths;
    }
    
    public List<String> getImprovements() {
        return improvements;
    }
    
    public void setImprovements(List<String> improvements) {
        this.improvements = improvements;
    }
    
    public boolean isCreative() {
        return isCreative;
    }
    
    public void setCreative(boolean creative) {
        isCreative = creative;
    }
    
    public String getError() {
        return error;
    }
    
    public void setError(String error) {
        this.error = error;
    }
}