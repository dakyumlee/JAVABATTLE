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
        System.out.println("세션 시작: 사용자 " + userId + ", 세션 " + sessionId);
        
        List<ActiveSession> existingSessions = activeSessionRepository.findByUserId(userId);
        for (ActiveSession session : existingSessions) {
            activeSessionRepository.delete(session);
        }
        activeSessionRepository.flush();
        
        ActiveSession session = new ActiveSession(userId, sessionId);
        session.setCurrentPage("/");
        session.setIsActive(true);
        session.setLastActivity(LocalDateTime.now());
        
        ActiveSession saved = activeSessionRepository.save(session);
        System.out.println("세션 생성 완료: " + saved.getId());
        return saved;
    }
    
    @Transactional
    public void updateActivity(Long userId, String page, String code, Boolean isCoding) {
        try {
            System.out.println("활동 업데이트: 사용자 " + userId + ", 페이지 " + page);
            
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
            }
        } catch (Exception e) {
            System.err.println("활동 업데이트 중 오류: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @Transactional
    public void endSession(Long userId) {
        try {
            System.out.println("세션 종료: 사용자 " + userId);
            
            List<ActiveSession> userSessions = activeSessionRepository.findByUserId(userId);
            for (ActiveSession session : userSessions) {
                session.setIsActive(false);
                session.setLastActivity(LocalDateTime.now());
                activeSessionRepository.save(session);
            }
            
            messagingTemplate.convertAndSend("/topic/student-disconnect", 
                Map.of("userId", userId));
            
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
            LocalDateTime cutoffTime = LocalDateTime.now().minusMinutes(10);
            List<ActiveSession> inactiveSessions = activeSessionRepository.findInactiveSessions(cutoffTime);
            
            for (ActiveSession session : inactiveSessions) {
                session.setIsActive(false);
                activeSessionRepository.save(session);
                
                messagingTemplate.convertAndSend("/topic/student-disconnect", 
                    Map.of("userId", session.getUserId()));
            }
            
        } catch (Exception e) {
            System.err.println("세션 정리 중 오류: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void sendActivityUpdate(ActiveSession session) {
        try {
            Map<String, Object> update = new HashMap<>();
            update.put("userId", session.getUserId());
            update.put("page", session.getCurrentPage());
            update.put("isCoding", session.getIsCoding());
            update.put("codeLength", session.getCurrentCode() != null ? session.getCurrentCode().length() : 0);
            update.put("timestamp", System.currentTimeMillis());
            
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
}