package com.javabattle.arena.repository;

import com.javabattle.arena.model.ProblemSubmission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ProblemSubmissionRepository extends JpaRepository<ProblemSubmission, Long> {
    
    @Query("SELECT p FROM ProblemSubmission p ORDER BY p.submittedAt DESC")
    List<ProblemSubmission> findAllOrderBySubmittedAtDesc();
    
    List<ProblemSubmission> findByUserIdOrderBySubmittedAtDesc(Long userId);
    
    List<ProblemSubmission> findByProblemTitleOrderBySubmittedAtDesc(String problemTitle);
    
    @Query("SELECT COUNT(p) FROM ProblemSubmission p WHERE p.score IS NOT NULL")
    long countGradedSubmissions();
    
    @Query("SELECT COUNT(p) FROM ProblemSubmission p WHERE p.score IS NULL")
    long countUngradedSubmissions();
    
    @Query("SELECT AVG(p.score) FROM ProblemSubmission p WHERE p.score IS NOT NULL")
    Double getAverageScore();

    Long countBySubmittedAtAfter(LocalDateTime start);
    
    Long countBySubmittedAtBetween(LocalDateTime start, LocalDateTime end);
    
    Long countByUserIdAndScoreIsNotNull(Long userId);
    
}