package com.javabattle.arena.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.Map;

@Service
public class SessionService {
    
    @Autowired
    private ActiveSessionRepository activeSessionRepository;
    
    @Transactional
    public void updateActivity(Map<String, Object> activityData) {
        try {
            System.out.println("세션 업데이트 시도 중단 - CLOB 문제로 인해 비활성화");
            // CLOB 문제 때문에 세션 업데이트 기능을 일시적으로 비활성화
            return;
            
        } catch (Exception e) {
            System.err.println("세션 업데이트 오류: " + e.getMessage());
        }
    }
    
    public void startSession(Long userId, String sessionId) {
        System.out.println("세션 시작 - 사용자 ID: " + userId);
        // 세션 시작 로직도 일시적으로 비활성화
    }
    
    public void endSession(Long userId) {
        System.out.println("세션 종료 - 사용자 ID: " + userId);
        // 세션 종료 로직도 일시적으로 비활성화
    }
}
