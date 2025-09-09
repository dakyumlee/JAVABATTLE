package com.javabattle.arena.model;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "active_sessions")
public class ActiveSession {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id")
    private Long userId;
    
    @Column(name = "session_id")
    private String sessionId;
    
    @Lob
    @Column(name = "current_code", columnDefinition = "CLOB")
    private String currentCode;
    
    @Column(name = "current_page")
    private String currentPage;
    
    @Column(name = "current_problem")
    private String currentProblem;
    
    @Column(name = "is_active")
    private Boolean isActive;
    
    @Column(name = "is_coding")
    private Boolean isCoding;
    
    @Column(name = "last_activity")
    private LocalDateTime lastActivity;
    
    @Column(name = "start_time")
    private LocalDateTime startTime;
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    
    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }
    
    public String getCurrentCode() { return currentCode; }
    public void setCurrentCode(String currentCode) { this.currentCode = currentCode; }
    
    public String getCurrentPage() { return currentPage; }
    public void setCurrentPage(String currentPage) { this.currentPage = currentPage; }
    
    public String getCurrentProblem() { return currentProblem; }
    public void setCurrentProblem(String currentProblem) { this.currentProblem = currentProblem; }
    
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    
    public Boolean getIsCoding() { return isCoding; }
    public void setIsCoding(Boolean isCoding) { this.isCoding = isCoding; }
    
    public LocalDateTime getLastActivity() { return lastActivity; }
    public void setLastActivity(LocalDateTime lastActivity) { this.lastActivity = lastActivity; }
    
    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
}
