package com.javabattle.arena.service;

import com.javabattle.arena.model.ActiveSession;
import com.javabattle.arena.repository.ActiveSessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class SessionService {
    
    @Autowired
    private ActiveSessionRepository activeSessionRepository;
    
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    
    public ActiveSession startSession(Long userId, String sessionId) {
        List<ActiveSession> existingSessions = activeSessionRepository.findByUserId(userId);
        for (ActiveSession session : existingSessions) {
            activeSessionRepository.delete(session);
        }
        activeSessionRepository.flush();
        
        ActiveSession session = new ActiveSession(userId, sessionId);
        session.setCurrentPage("/");
        session.setIsActive(true);
        session.setLastActivity(LocalDateTime.now());
        return activeSessionRepository.save(session);
    }
    
    @Transactional
    public void updateActivity(Long userId, String page, String code, Boolean isCoding) {
        try {
            System.out.println("=== 활동 업데이트 요청 ===");
            System.out.println("사용자 ID: " + userId);
            System.out.println("페이지: " + page);
            System.out.println("코딩 여부: " + isCoding);
            
            Optional<ActiveSession> sessionOpt = activeSessionRepository.findByUserIdAndIsActiveTrue(userId);
            
            if (sessionOpt.isPresent()) {
                ActiveSession session = sessionOpt.get();
                session.setLastActivity(LocalDateTime.now());
                session.setCurrentPage(page);
                
                if (code != null && code.length() < 500) {
                    session.setCurrentCode(code);
                }
                session.setIsCoding(isCoding != null ? isCoding : false);
                
                activeSessionRepository.save(session);
                System.out.println("활동 업데이트 완료: " + session.getSessionId());
                
                sendActivityUpdate(session);
            } else {
                System.out.println("활성 세션을 찾을 수 없음: " + userId);
                System.out.println("현재 활성 세션 목록:");
                List<ActiveSession> allSessions = activeSessionRepository.findByUserId(userId);
                for (ActiveSession s : allSessions) {
                    System.out.println("- 세션: " + s.getSessionId() + ", 활성: " + s.getIsActive());
                }
            }
        } catch (Exception e) {
            System.err.println("활동 업데이트 중 오류: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @Transactional
    public void endSession(Long userId) {
        try {
            System.out.println("=== 세션 종료 요청 ===");
            System.out.println("사용자 ID: " + userId);
            
            List<ActiveSession> userSessions = activeSessionRepository.findByUserId(userId);
            System.out.println("종료할 세션 개수: " + userSessions.size());
            
            for (ActiveSession session : userSessions) {
                session.setIsActive(false);
                session.setLastActivity(LocalDateTime.now());
                activeSessionRepository.save(session);
                System.out.println("세션 비활성화: " + session.getSessionId());
            }
            
            messagingTemplate.convertAndSend("/topic/student-disconnect", 
                new SessionEndNotification(userId));
            System.out.println("세션 종료 알림 전송 완료");
            
        } catch (Exception e) {
            System.err.println("세션 종료 중 오류: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public List<ActiveSession> getActiveSessions() {
        try {
            List<ActiveSession> sessions = activeSessionRepository.findByIsActiveTrue();
            System.out.println("현재 활성 세션 개수: " + sessions.size());
            return sessions;
        } catch (Exception e) {
            System.err.println("활성 세션 조회 중 오류: " + e.getMessage());
            e.printStackTrace();
            return List.of();
        }
    }
    
    public long getActiveSessionCount() {
        try {
            return activeSessionRepository.countActiveSessions();
        } catch (Exception e) {
            System.err.println("활성 세션 수 조회 중 오류: " + e.getMessage());
            return 0;
        }
    }
    
    public long getCodingSessionCount() {
        try {
            return activeSessionRepository.countCodingSessions();
        } catch (Exception e) {
            System.err.println("코딩 세션 수 조회 중 오류: " + e.getMessage());
            return 0;
        }
    }
    
    @Scheduled(fixedRate = 60000)
    @Transactional
    public void cleanupInactiveSessions() {
        try {
            System.out.println("=== 비활성 세션 정리 시작 ===");
            LocalDateTime cutoffTime = LocalDateTime.now().minusMinutes(10);
            
            List<ActiveSession> inactiveSessions = activeSessionRepository.findInactiveSessions(cutoffTime);
            System.out.println("정리할 비활성 세션 개수: " + inactiveSessions.size());
            
            for (ActiveSession session : inactiveSessions) {
                session.setIsActive(false);
                activeSessionRepository.save(session);
                System.out.println("비활성화된 세션: " + session.getSessionId());
                
                messagingTemplate.convertAndSend("/topic/student-disconnect", 
                    new SessionEndNotification(session.getUserId()));
            }
            
            System.out.println("비활성 세션 정리 완료");
            
        } catch (Exception e) {
            System.err.println("세션 정리 중 오류: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void sendActivityUpdate(ActiveSession session) {
        try {
            ActivityUpdate update = new ActivityUpdate(
                session.getUserId(),
                session.getCurrentPage(),
                session.getIsCoding(),
                session.getCurrentCode() != null ? session.getCurrentCode().length() : 0,
                System.currentTimeMillis()
            );
            
            messagingTemplate.convertAndSend("/topic/teacher-monitor", update);
            System.out.println("활동 업데이트 전송 완료: " + session.getUserId());
            
        } catch (Exception e) {
            System.err.println("활동 업데이트 전송 중 오류: " + e.getMessage());
            e.printStackTrace();
            
            Map<String, String> errorMap = new HashMap<>();
            errorMap.put("error", "Failed to process activity");
            messagingTemplate.convertAndSend("/topic/teacher-monitor", errorMap);
        }
    }
    
    public static class ActivityUpdate {
        public Long userId;
        public String page;
        public Boolean isCoding;
        public Integer codeLength;
        public Long timestamp;
        
        public ActivityUpdate(Long userId, String page, Boolean isCoding, Integer codeLength, Long timestamp) {
            this.userId = userId;
            this.page = page;
            this.isCoding = isCoding;
            this.codeLength = codeLength;
            this.timestamp = timestamp;
        }
    }
    
    public static class SessionEndNotification {
        public Long userId;
        
        public SessionEndNotification(Long userId) {
            this.userId = userId;
        }
    }
}