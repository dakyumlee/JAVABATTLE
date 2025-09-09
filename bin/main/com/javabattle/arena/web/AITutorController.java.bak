package com.javabattle.arena.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.javabattle.arena.service.AITutorService;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/ai-tutor")
@CrossOrigin(origins = "*")
public class AITutorController {
    
    @Autowired
    private AITutorService aiTutorService;
    
    @PostMapping("/ask")
    public ResponseEntity<Map<String, Object>> askQuestion(
            @RequestBody Map<String, String> request) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            String question = request.get("question");
            String answer = aiTutorService.getAnswer(question);
            
            response.put("success", true);
            response.put("answer", answer);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "AI 튜터 서비스에 오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    
    @PostMapping("/review-code")
    public ResponseEntity<Map<String, Object>> reviewCode(
            @RequestBody Map<String, String> request) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            String code = request.get("code");
            String language = request.get("language");
            
            String review = aiTutorService.reviewCode(code, language);
            
            response.put("success", true);
            response.put("review", review);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "코드 리뷰 중 오류가 발생했습니다.");
            return ResponseEntity.status(500).body(response);
        }
    }
}