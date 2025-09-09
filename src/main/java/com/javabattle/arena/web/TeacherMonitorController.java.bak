package com.javabattle.arena.web;

import com.javabattle.arena.model.ActiveSession;
import com.javabattle.arena.service.SessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Controller
public class TeacherMonitorController {
    
    @Autowired
    private SessionService sessionService;
    
    @MessageMapping("/student/activity")
    @SendTo("/topic/teacher-monitor")
    public Map<String, Object> handleStudentActivity(Map<String, Object> activity) {
        try {
            Long userId = Long.valueOf(activity.get("userId").toString());
            String page = (String) activity.get("page");
            String code = (String) activity.get("code");
            Boolean isCoding = (Boolean) activity.get("isCoding");
            
            sessionService.updateActivity(userId, page, code, isCoding);
            
            Map<String, Object> response = new HashMap<>();
            response.put("userId", userId);
            response.put("page", page);
            response.put("isCoding", isCoding);
            response.put("codeLength", code != null ? code.length() : 0);
            response.put("timestamp", System.currentTimeMillis());
            
            return response;
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to process activity");
            return error;
        }
    }
    
    @MessageMapping("/student/connect")
    @SendTo("/topic/teacher-monitor")
    public Map<String, Object> handleStudentConnect(Map<String, Object> connectData) {
        try {
            Long userId = Long.valueOf(connectData.get("userId").toString());
            String sessionId = (String) connectData.get("sessionId");
            
            ActiveSession session = sessionService.startSession(userId, sessionId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("type", "STUDENT_CONNECTED");
            response.put("userId", userId);
            response.put("sessionId", session.getSessionId());
            response.put("timestamp", System.currentTimeMillis());
            
            return response;
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to connect student");
            return error;
        }
    }
    
    @MessageMapping("/student/disconnect")
    @SendTo("/topic/student-disconnect")
    public Map<String, Object> handleStudentDisconnect(Map<String, Object> disconnectData) {
        try {
            Long userId = Long.valueOf(disconnectData.get("userId").toString());
            
            sessionService.endSession(userId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("type", "STUDENT_DISCONNECTED");
            response.put("userId", userId);
            response.put("timestamp", System.currentTimeMillis());
            
            return response;
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to disconnect student");
            return error;
        }
    }
    
    @MessageMapping("/teacher/send-hint")
    public void sendHintToStudent(Map<String, Object> hintData) {
        try {
            Long studentId = Long.valueOf(hintData.get("studentId").toString());
            String message = (String) hintData.get("message");
            
            Map<String, Object> hint = new HashMap<>();
            hint.put("type", "HINT");
            hint.put("message", message);
            hint.put("from", "teacher");
            hint.put("timestamp", LocalDateTime.now());
            
        } catch (Exception e) {
            System.err.println("Failed to send hint: " + e.getMessage());
        }
    }
}
