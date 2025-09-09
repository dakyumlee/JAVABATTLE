package com.javabattle.arena.repository;

import com.javabattle.arena.model.Problem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProblemRepository extends JpaRepository<Problem, Long> {
    List<Problem> findByDifficulty(String difficulty);
    List<Problem> findByCategory(String category);
    List<Problem> findByDifficultyAndCategory(String difficulty, String category);
    
    @Query("SELECT DISTINCT p.category FROM Problem p ORDER BY p.category")
    List<String> findDistinctCategories();
}