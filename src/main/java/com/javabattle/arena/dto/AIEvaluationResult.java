package com.javabattle.arena.dto;

import java.util.List;

public class AIEvaluationResult {
    private boolean isCorrect;
    private int score;
    private String feedback;
    private List<String> strengths;
    private List<String> improvements;
    private boolean isCreative;
    private long evaluationTime;
    
    public AIEvaluationResult() {}
    
    public static AIEvaluationResultBuilder builder() {
        return new AIEvaluationResultBuilder();
    }
    
    public static class AIEvaluationResultBuilder {
        private AIEvaluationResult result = new AIEvaluationResult();
        
        public AIEvaluationResultBuilder isCorrect(boolean isCorrect) {
            result.isCorrect = isCorrect;
            return this;
        }
        
        public AIEvaluationResultBuilder score(int score) {
            result.score = score;
            return this;
        }
        
        public AIEvaluationResultBuilder feedback(String feedback) {
            result.feedback = feedback;
            return this;
        }
        
        public AIEvaluationResultBuilder strengths(List<String> strengths) {
            result.strengths = strengths;
            return this;
        }
        
        public AIEvaluationResultBuilder improvements(List<String> improvements) {
            result.improvements = improvements;
            return this;
        }
        
        public AIEvaluationResultBuilder isCreative(boolean isCreative) {
            result.isCreative = isCreative;
            return this;
        }
        
        public AIEvaluationResultBuilder evaluationTime(long evaluationTime) {
            result.evaluationTime = evaluationTime;
            return this;
        }
        
        public AIEvaluationResult build() {
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
    
    public long getEvaluationTime() {
        return evaluationTime;
    }
    
    public void setEvaluationTime(long evaluationTime) {
        this.evaluationTime = evaluationTime;
    }
}