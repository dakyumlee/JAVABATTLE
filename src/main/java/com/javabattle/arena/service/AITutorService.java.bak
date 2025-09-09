package com.javabattle.arena.service;

import org.springframework.stereotype.Service;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import java.util.*;

@Service
public class AITutorService {
    
    private static final String CLAUDE_API_URL = "https://api.anthropic.com/v1/messages";
    private static final String API_KEY = "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";
    
    private RestTemplate restTemplate = new RestTemplate();
    
    public String getAnswer(String question) {
        try {
            String prompt = "당신은 친근하고 도움이 되는 Java 전문가입니다. 자연스럽게 대화하며 질문에 답변해주세요.\n\n질문: " + question;
            return callClaudeAPI(prompt);
        } catch (Exception e) {
            e.printStackTrace();
            return "죄송합니다. AI 서비스 연결에 문제가 있습니다: " + e.getMessage();
        }
    }
    
    public String reviewCode(String code, String language) {
        try {
            String prompt = "당신은 " + language + " 코드 리뷰 전문가입니다. 다음 코드를 분석하고 개선점을 제안해주세요:\n\n```" + language + "\n" + code + "\n```";
            return callClaudeAPI(prompt);
        } catch (Exception e) {
            e.printStackTrace();
            return "코드 리뷰 중 오류가 발생했습니다: " + e.getMessage();
        }
    }
    
    private String callClaudeAPI(String prompt) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("x-api-key", API_KEY);
            headers.set("anthropic-version", "2023-06-01");
            
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", "claude-3-5-sonnet-20241022");
            requestBody.put("max_tokens", 1000);
            
            List<Map<String, String>> messages = new ArrayList<>();
            Map<String, String> message = new HashMap<>();
            message.put("role", "user");
            message.put("content", prompt);
            messages.add(message);
            requestBody.put("messages", messages);
            
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(CLAUDE_API_URL, entity, Map.class);
            
            if (response.getStatusCode() == HttpStatus.OK) {
                Map<String, Object> responseBody = response.getBody();
                List<Map<String, Object>> content = (List<Map<String, Object>>) responseBody.get("content");
                if (content != null && !content.isEmpty()) {
                    return (String) content.get(0).get("text");
                }
            }
            
            return "API 응답을 처리할 수 없습니다. 상태: " + response.getStatusCode();
            
        } catch (Exception e) {
            throw new RuntimeException("Claude API 호출 실패: " + e.getMessage(), e);
        }
    }
}