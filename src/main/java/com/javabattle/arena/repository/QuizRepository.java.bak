package com.javabattle.arena.repository;

import com.javabattle.arena.model.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, Long> {
    List<Quiz> findByIsActiveTrueOrderByCreatedAtDesc();
    List<Quiz> findByCreatedByOrderByCreatedAtDesc(Long createdBy);
    
    @Query("SELECT q FROM Quiz q WHERE q.isActive = true AND q.scheduleType = 'now'")
    List<Quiz> findActiveQuizzes();
}
