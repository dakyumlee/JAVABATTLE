package com.javabattle.arena.repository;

import com.javabattle.arena.model.QuickProblem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface QuickProblemRepository extends JpaRepository<QuickProblem, Long> {
    List<QuickProblem> findByTeacherIdOrderByCreatedAtDesc(Long teacherId);
    List<QuickProblem> findByIsActiveTrue();
}
