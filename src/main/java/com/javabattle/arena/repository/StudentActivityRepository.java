package com.javabattle.arena.repository;

import com.javabattle.arena.model.StudentActivity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;

public interface StudentActivityRepository extends JpaRepository<StudentActivity, Long> {
    
    List<StudentActivity> findByStudentIdAndTimestampBetween(Long studentId, LocalDateTime start, LocalDateTime end);
    
    List<StudentActivity> findByActivityTypeAndTimestampBetween(String activityType, LocalDateTime start, LocalDateTime end);
    
    @Query("SELECT COUNT(DISTINCT s.studentId) FROM StudentActivity s WHERE s.timestamp >= :start")
    Long countActiveStudentsSince(@Param("start") LocalDateTime start);
    
    @Query("SELECT s.activityType, COUNT(s) FROM StudentActivity s WHERE s.timestamp >= :start GROUP BY s.activityType")
    List<Object[]> getActivityStatsSince(@Param("start") LocalDateTime start);
    
    @Query("SELECT COUNT(s) FROM StudentActivity s WHERE s.studentId = :studentId AND s.activityType = 'LOGIN' AND s.timestamp >= :start")
    Long countLoginsSince(@Param("studentId") Long studentId, @Param("start") LocalDateTime start);
    
    @Query("SELECT MAX(s.timestamp) FROM StudentActivity s WHERE s.studentId = :studentId")
    LocalDateTime findTopByStudentIdOrderByTimestampDesc(@Param("studentId") Long studentId);
}