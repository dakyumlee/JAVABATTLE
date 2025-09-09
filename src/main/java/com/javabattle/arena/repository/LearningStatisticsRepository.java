package com.javabattle.arena.repository;

import com.javabattle.arena.model.LearningStatistics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface LearningStatisticsRepository extends JpaRepository<LearningStatistics, Long> {
    
    Optional<LearningStatistics> findByUserId(Long userId);
    
    @Query("SELECT ls FROM LearningStatistics ls ORDER BY ls.problemsSolved DESC")
    List<LearningStatistics> findTopPerformers();
    
    @Query("SELECT AVG(ls.problemsSolved) FROM LearningStatistics ls")
    Double getAverageProblemsSolved();
    
    @Query("SELECT AVG(ls.totalStudyTime) FROM LearningStatistics ls")
    Double getAverageStudyTime();
    
    @Query("SELECT COUNT(ls) FROM LearningStatistics ls WHERE ls.lastActivity >= :since")
    Long getActiveUsersCount(@Param("since") LocalDateTime since);
}
