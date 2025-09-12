package com.javabattle.arena.service;

import com.javabattle.arena.dto.AIEvaluationResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
public class ClaudeCodeEvaluationService {
    
    @Value("${claude.api.key}")
    private String claudeApiKey;
    
    private final RestTemplate restTemplate = new RestTemplate();
    
    public AIEvaluationResult evaluateCode(String studentCode, String problemDescription, String expectedConcepts) {
        try {
            String prompt = createEvaluationPrompt(studentCode, problemDescription, expectedConcepts);
            String response = callClaude(prompt);
            return parseAIResponse(response);
        } catch (Exception e) {
            return fallbackEvaluation(studentCode, expectedConcepts);
        }
    }
    
    private String createEvaluationPrompt(String studentCode, String problemDescription, String expectedConcepts) {
        return String.format("""
            당신은 Java 프로그래밍 교육 전문가입니다. 학생의 코드를 평가해주세요.
            
            **문제 설명:**
            %s
            
            **기대되는 핵심 개념:**
            %s
            
            **학생 답안:**
            ```java
            %s
            ```
            
            **평가 기준:**
            1. 문제 요구사항 충족 여부
            2. 코드 문법 정확성
            3. 핵심 개념 사용 여부
            4. 창의적이고 다른 방법이라도 논리적으로 올바른지
            
            **응답 형식 (JSON만 응답해주세요):**
            {
                "isCorrect": true,
                "score": 95,
                "feedback": "구체적인 피드백",
                "strengths": ["잘한 점1", "잘한 점2"],
                "improvements": ["개선점1", "개선점2"],
                "isCreative": true
            }
            """, problemDescription, expectedConcepts, studentCode);
    }
    
    private String callClaude(String prompt) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("x-api-key", claudeApiKey);
        headers.set("anthropic-version", "2023-06-01");
        
        Map<String, Object> requestBody = Map.of(
            "model", "claude-3-sonnet-20240229",
            "max_tokens", 1000,
            "messages", List.of(
                Map.of("role", "user", "content", prompt)
            )
        );
        
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
        
        ResponseEntity<Map> response = restTemplate.postForEntity(
            "https://api.anthropic.com/v1/messages", 
            request, 
            Map.class
        );
        
        Map<String, Object> responseBody = response.getBody();
        List<Map<String, Object>> content = (List<Map<String, Object>>) responseBody.get("content");
        
        return (String) content.get(0).get("text");
    }
    
    private AIEvaluationResult parseAIResponse(String response) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            String jsonPart = extractJsonFromResponse(response);
            return mapper.readValue(jsonPart, AIEvaluationResult.class);
        } catch (Exception e) {
            return AIEvaluationResult.builder()
                .isCorrect(false)
                .score(0)
                .feedback("AI 평가 중 오류가 발생했습니다.")
                .build();
        }
    }
    
    private String extractJsonFromResponse(String response) {
        int startIndex = response.indexOf("{");
        int endIndex = response.lastIndexOf("}") + 1;
        
        if (startIndex != -1 && endIndex > startIndex) {
            return response.substring(startIndex, endIndex);
        }
        
        throw new RuntimeException("Valid JSON not found in AI response");
    }
    
    private AIEvaluationResult fallbackEvaluation(String studentCode, String expectedConcepts) {
        boolean containsKeywords = Arrays.stream(expectedConcepts.split(","))
            .map(String::trim)
            .anyMatch(keyword -> studentCode.toLowerCase().contains(keyword.toLowerCase()));
        
        return AIEvaluationResult.builder()
            .isCorrect(containsKeywords)
            .score(containsKeywords ? 70 : 0)
            .feedback(containsKeywords ? "기본 요구사항을 충족합니다." : "핵심 개념이 누락되었습니다.")
            .isCreative(false)
            .build();
    }
}