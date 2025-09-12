package com.javabattle.arena.web;

import com.javabattle.arena.model.ActiveSession;
import com.javabattle.arena.model.ProblemSubmission;
import com.javabattle.arena.model.QuizSubmission;
import com.javabattle.arena.repository.ProblemSubmissionRepository;
import com.javabattle.arena.repository.QuizSubmissionRepository;
import com.javabattle.arena.service.SessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
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
            System.out.println("=== 세션 시작 API 호출 ===");
            System.out.println("요청 데이터: " + request);
            
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
            
            System.out.println("처리된 사용자 ID: " + userId + ", 세션 ID: " + sessionId);
            
            ActiveSession session = sessionService.startSession(userId, sessionId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("sessionId", session.getSessionId());
            response.put("userId", userId);
            response.put("message", "Session started successfully");
            
            System.out.println("세션 시작 완료: " + response);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("세션 시작 실패: " + e.getMessage());
            e.printStackTrace();
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to start session: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @MessageMapping("/student/activity")
    public void handleStudentActivity(@Payload Map<String, Object> payload) {
        try {
            System.out.println("=== WebSocket 활동 메시지 수신 ===");
            System.out.println("페이로드: " + payload);
            
            Long userId = Long.valueOf(payload.get("userId").toString());
            String page = (String) payload.get("page");
            String code = (String) payload.get("code");
            Boolean isCoding = (Boolean) payload.get("isCoding");
            
            System.out.println("사용자 " + userId + " 활동: " + page + " (코딩: " + isCoding + ")");
            
            if (code != null && code.length() > 100) {
                code = code.substring(0, 100);
            }
            
            sessionService.updateActivity(userId, page, code, isCoding);
            
        } catch (Exception e) {
            System.err.println("WebSocket 활동 처리 중 오류: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @PostMapping("/update")
    public ResponseEntity<Map<String, Object>> updateActivity(@RequestBody Map<String, Object> request) {
        try {
            System.out.println("=== REST 활동 업데이트 요청 ===");
            System.out.println("요청 데이터: " + request);
            
            Object userIdObj = request.get("userId");
            String page = (String) request.get("page");
            String code = (String) request.get("code");
            Boolean isCoding = (Boolean) request.get("isCoding");
            
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
            
            if (code != null && code.length() > 100) {
                code = code.substring(0, 100);
            }
            
            sessionService.updateActivity(userId, page, code, isCoding);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Activity updated successfully");
            response.put("timestamp", LocalDateTime.now());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("활동 업데이트 실패: " + e.getMessage());
            e.printStackTrace();
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to update activity: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @PostMapping("/end")
    public ResponseEntity<Map<String, Object>> endSession(@RequestBody Map<String, Object> request) {
        try {
            System.out.println("=== 세션 종료 API 호출 ===");
            System.out.println("요청 데이터: " + request);
            
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
            
            System.out.println("사용자 " + userId + " 세션 종료 처리");
            
            sessionService.endSession(userId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Session ended successfully");
            
            System.out.println("세션 종료 완료: " + response);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("세션 종료 실패: " + e.getMessage());
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
            System.out.println("=== 활성 세션 조회 API 호출 ===");
            List<ActiveSession> sessions = sessionService.getActiveSessions();
            System.out.println("조회된 활성 세션 수: " + sessions.size());
            return ResponseEntity.ok(sessions);
        } catch (Exception e) {
            System.err.println("활성 세션 조회 실패: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getSessionStats() {
        try {
            System.out.println("=== 세션 통계 조회 API 호출 ===");
            
            long activeCount = sessionService.getActiveSessionCount();
            long codingCount = sessionService.getCodingSessionCount();
            
            Map<String, Object> stats = new HashMap<>();
            stats.put("activeCount", activeCount);
            stats.put("codingCount", codingCount);
            stats.put("timestamp", LocalDateTime.now());
            
            System.out.println("세션 통계: 활성=" + activeCount + ", 코딩=" + codingCount);
            
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            System.err.println("세션 통계 조회 실패: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/debug/{userId}")
    public ResponseEntity<Map<String, Object>> debugUserSessions(@PathVariable Long userId) {
        try {
            System.out.println("=== 사용자 세션 디버그: " + userId + " ===");
            
            List<ActiveSession> allSessions = sessionService.getActiveSessions();
            List<ActiveSession> userSessions = allSessions.stream()
                .filter(s -> s.getUserId().equals(userId))
                .toList();
            
            Map<String, Object> debug = new HashMap<>();
            debug.put("userId", userId);
            debug.put("totalActiveSessions", allSessions.size());
            debug.put("userActiveSessions", userSessions.size());
            debug.put("userSessions", userSessions);
            debug.put("timestamp", LocalDateTime.now());
            
            System.out.println("디버그 결과: " + debug);
            
            return ResponseEntity.ok(debug);
        } catch (Exception e) {
            System.err.println("세션 디버그 실패: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }
}