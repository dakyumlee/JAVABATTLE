package com.javabattle.arena.domain.submission;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubmissionRepository extends JpaRepository<Submission, Long> {
    List<Submission> findByPlayerIdOrderByCreatedAtDesc(Long playerId);
    List<Submission> findByChallengeIdOrderByCreatedAtDesc(Long challengeId);
}
