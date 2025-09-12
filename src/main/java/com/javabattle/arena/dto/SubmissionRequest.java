package com.javabattle.arena.dto;

public class SubmissionRequest {
    private String answer;
    private boolean useAI = true;
    
    public SubmissionRequest() {}
    
    public SubmissionRequest(String answer) {
        this.answer = answer;
    }
    
    public String getAnswer() {
        return answer;
    }
    
    public void setAnswer(String answer) {
        this.answer = answer;
    }
    
    public boolean isUseAI() {
        return useAI;
    }
    
    public void setUseAI(boolean useAI) {
        this.useAI = useAI;
    }
}