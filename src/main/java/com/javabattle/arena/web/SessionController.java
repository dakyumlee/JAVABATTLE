package com.javabattle.arena.web;

import com.javabattle.arena.model.ActiveSession;
import com.javabattle.arena.model.ProblemSubmission;
import com.javabattle.arena.model.QuizSubmission;
import com.javabattle.arena.repository.ProblemSubmissionRepository;
import com.javabattle.arena.repository.QuizSubmissionRepository;
import com.javabattle.arena.service.SessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/session")
public class SessionController {
    
    @Autowired
    private SessionService sessionService;
    
    @Autowired
    private ProblemSubmissionRepository problemSubmissionRepository;
    
    @Autowired
    private QuizSubmissionRepository quizSubmissionRepository;
    
    @PostMapping("/start")
    public ResponseEntity<Map<String, Object>> startSession(@RequestBody Map<String, Object> request) {
        try {
            Object userIdObj = request.get("userId");
            String sessionId = (String) request.get("sessionId");
            
            Long userId = null;
            if (userIdObj instanceof Integer) {
                userId = ((Integer) userIdObj).longValue();
            } else if (userIdObj instanceof Long) {
                userId = (Long) userIdObj;
            } else if (userIdObj instanceof String) {
                userId = Long.valueOf((String) userIdObj);
            } else {
                userId = Long.valueOf(userIdObj.toString());
            }
            
            if (sessionId == null || sessionId.isEmpty()) {
                sessionId = "session-" + System.currentTimeMillis();
            }
            
            ActiveSession session = sessionService.startSession(userId, sessionId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("sessionId", session.getSessionId());
            response.put("message", "Session started successfully");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to start session: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @PostMapping("/update")
    public ResponseEntity<Map<String, Object>> updateActivity(@RequestBody Map<String, Object> request) {
        System.out.println("=== 세션 업데이트 요청 무시됨 ===");
        System.out.println("요청 데이터: " + request);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "세션 업데이트 임시 비활성화 (CLOB 오류 회피)");
        response.put("timestamp", LocalDateTime.now());
        
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/end")
    public ResponseEntity<Map<String, Object>> endSession(@RequestBody Map<String, Object> request) {
        try {
            Object userIdObj = request.get("userId");
            
            Long userId = null;
            if (userIdObj instanceof Integer) {
                userId = ((Integer) userIdObj).longValue();
            } else if (userIdObj instanceof Long) {
                userId = (Long) userIdObj;
            } else if (userIdObj instanceof String) {
                userId = Long.valueOf((String) userIdObj);
            } else {
                userId = Long.valueOf(userIdObj.toString());
            }
            
            sessionService.endSession(userId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Session ended");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to end session: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @GetMapping("/active")
    public ResponseEntity<List<ActiveSession>> getActiveSessions() {
        try {
            List<ActiveSession> sessions = sessionService.getActiveSessions();
            return ResponseEntity.ok(sessions);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getSessionStats() {
        try {
            Map<String, Object> stats = new HashMap<>();
            stats.put("activeCount", sessionService.getActiveSessionCount());
            stats.put("codingCount", sessionService.getCodingSessionCount());
            stats.put("timestamp", LocalDateTime.now());
            
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }
}