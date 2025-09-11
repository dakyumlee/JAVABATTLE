package com.javabattle.arena.repository;

import com.javabattle.arena.model.QuizSubmission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface QuizSubmissionRepository extends JpaRepository<QuizSubmission, Long> {

    @Query("SELECT q FROM QuizSubmission q ORDER BY q.submittedAt DESC")
    List<QuizSubmission> findAllOrderBySubmittedAtDesc();

    List<QuizSubmission> findByUserIdOrderBySubmittedAtDesc(Long userId);

    List<QuizSubmission> findByQuizTitleOrderBySubmittedAtDesc(String quizTitle);

    @Query("SELECT COUNT(q) FROM QuizSubmission q WHERE q.isCorrect = 1")
    long countCorrectAnswers();

    @Query("SELECT COUNT(q) FROM QuizSubmission q")
    long countTotalAnswers();

    @Query("SELECT q.userId, COUNT(q) as total, SUM(q.isCorrect) as correct FROM QuizSubmission q GROUP BY q.userId")
    List<Object[]> getQuizStatsByUser();

    @Query("SELECT AVG(q.isCorrect) FROM QuizSubmission q WHERE q.userId = :userId")
    Double getAverageScoreByUserId(@Param("userId") Long userId);

    Long countByUserId(Long userId);
    
    // 이 메서드 추가
    Long countBySubmittedAtAfter(LocalDateTime submittedAt);
}