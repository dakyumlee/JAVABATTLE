package com.javabattle.arena.service;

import com.javabattle.arena.model.ActiveSession;
import com.javabattle.arena.repository.ActiveSessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class SessionService {
    
    @Autowired
    private ActiveSessionRepository activeSessionRepository;
    
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    
    public ActiveSession startSession(Long userId, String sessionId) {
        deactivateUserSessions(userId);
        
        ActiveSession session = new ActiveSession(userId, sessionId);
        session.setCurrentPage("/");
        session.setIsActive(true);
        session.setLastActivity(LocalDateTime.now());
        return activeSessionRepository.save(session);
    }
    
    public void updateActivity(Long userId, String page, String code, Boolean isCoding) {
        Optional<ActiveSession> sessionOpt = activeSessionRepository.findByUserIdAndIsActiveTrue(userId);
        
        if (sessionOpt.isPresent()) {
            ActiveSession session = sessionOpt.get();
            session.setLastActivity(LocalDateTime.now());
            session.setCurrentPage(page);
            if (code != null && code.length() < 1000) {
                session.setCurrentCode(code);
            }
            session.setIsCoding(isCoding != null ? isCoding : false);
            activeSessionRepository.save(session);
            
            sendActivityUpdate(session);
        } else {
            startSession(userId, "auto-" + System.currentTimeMillis());
        }
    }
    
    public void endSession(Long userId) {
        deactivateUserSessions(userId);
        
        messagingTemplate.convertAndSend("/topic/student-disconnect", 
            new SessionEndNotification(userId));
    }
    
    public List<ActiveSession> getActiveSessions() {
        return activeSessionRepository.findByIsActiveTrue();
    }
    
    public long getActiveSessionCount() {
        return activeSessionRepository.countActiveSessions();
    }
    
    public long getCodingSessionCount() {
        return activeSessionRepository.countCodingSessions();
    }
    
    @Scheduled(fixedRate = 60000)
    public void cleanupInactiveSessions() {
        LocalDateTime cutoffTime = LocalDateTime.now().minusMinutes(10);
        List<ActiveSession> inactiveSessions = activeSessionRepository.findInactiveSessions(cutoffTime);
        
        for (ActiveSession session : inactiveSessions) {
            endSession(session.getUserId());
        }
        
        int deactivated = activeSessionRepository.deactivateOldSessions(cutoffTime);
        if (deactivated > 0) {
            System.out.println("Deactivated " + deactivated + " inactive sessions");
        }
    }
    
    private void deactivateUserSessions(Long userId) {
        activeSessionRepository.deactivateUserSessions(userId);
    }
    
    private void sendActivityUpdate(ActiveSession session) {
        ActivityUpdate update = new ActivityUpdate(
            session.getUserId(),
            session.getCurrentPage(),
            session.getIsCoding(),
            session.getCurrentCode() != null ? session.getCurrentCode().length() : 0,
            System.currentTimeMillis()
        );
        
        messagingTemplate.convertAndSend("/topic/teacher-monitor", update);
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