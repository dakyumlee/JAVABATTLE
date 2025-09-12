package com.javabattle.arena.repository;

import com.javabattle.arena.model.ActiveSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ActiveSessionRepository extends JpaRepository<ActiveSession, Long> {
    
    @Query("SELECT a FROM ActiveSession a WHERE a.userId = :userId AND a.isActive = true")
    Optional<ActiveSession> findByUserIdAndIsActiveTrue(@Param("userId") Long userId);
    
    @Query("SELECT a FROM ActiveSession a WHERE a.isActive = true ORDER BY a.lastActivity DESC")
    List<ActiveSession> findByIsActiveTrue();
    
    @Query("SELECT COUNT(a) FROM ActiveSession a WHERE a.isActive = true")
    long countActiveSessions();
    
    @Query("SELECT COUNT(a) FROM ActiveSession a WHERE a.isActive = true AND a.isCoding = true")
    long countCodingSessions();
    
    @Query("SELECT a FROM ActiveSession a WHERE a.isActive = true AND a.lastActivity < :cutoffTime")
    List<ActiveSession> findInactiveSessions(@Param("cutoffTime") LocalDateTime cutoffTime);
    
    @Modifying
    @Transactional
    @Query("UPDATE ActiveSession a SET a.isActive = false WHERE a.userId = :userId")
    int deactivateUserSessions(@Param("userId") Long userId);
    
    @Modifying
    @Transactional
    @Query("UPDATE ActiveSession a SET a.isActive = false WHERE a.lastActivity < :cutoffTime")
    int deactivateOldSessions(@Param("cutoffTime") LocalDateTime cutoffTime);
    
    @Query("SELECT a FROM ActiveSession a WHERE a.userId = :userId ORDER BY a.lastActivity DESC")
    List<ActiveSession> findByUserId(@Param("userId") Long userId);
    
    @Modifying
    @Query("DELETE FROM ActiveSession a WHERE a.userId = :userId AND a.isActive = true")
    void deleteByUserIdAndIsActiveTrue(@Param("userId") Long userId);
}