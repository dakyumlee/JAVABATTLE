package com.javabattle.arena.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "active_sessions")
public class ActiveSession {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "session_id", length = 100)
    private String sessionId;

    @Column(name = "current_page", length = 100)
    private String currentPage;

    @Column(name = "current_code", columnDefinition = "TEXT")
    private String currentCode;

    @Column(name = "last_activity")
    private LocalDateTime lastActivity;

    @Column(name = "is_coding")
    private Boolean isCoding = false;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "current_problem", length = 100)
    private String currentProblem;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "code_length")
    private Long codeLength = 0L;

    public ActiveSession() {
        this.startTime = LocalDateTime.now();
        this.lastActivity = LocalDateTime.now();
    }

    public ActiveSession(Long userId, String sessionId) {
        this();
        this.userId = userId;
        this.sessionId = sessionId;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }

    public String getCurrentPage() { return currentPage; }
    public void setCurrentPage(String currentPage) { this.currentPage = currentPage; }

    public String getCurrentCode() { return currentCode; }
    public void setCurrentCode(String currentCode) { this.currentCode = currentCode; }

    public LocalDateTime getLastActivity() { return lastActivity; }
    public void setLastActivity(LocalDateTime lastActivity) { this.lastActivity = lastActivity; }

    public Boolean getIsCoding() { return isCoding; }
    public void setIsCoding(Boolean isCoding) { this.isCoding = isCoding; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

    public String getCurrentProblem() { return currentProblem; }
    public void setCurrentProblem(String currentProblem) { this.currentProblem = currentProblem; }

    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

    public Long getCodeLength() { return codeLength; }
    public void setCodeLength(Long codeLength) { this.codeLength = codeLength; }
}