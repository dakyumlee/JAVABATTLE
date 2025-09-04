package com.javabattle.arena.judge.executor;

import com.javabattle.arena.domain.challenge.Challenge;
import com.javabattle.arena.domain.submission.Submission;
import lombok.Getter;
import org.springframework.stereotype.Service;

@Service
public class JudgeService {
    
    public JudgeResult judge(Challenge challenge, String code) {
        if (code == null || code.trim().isEmpty()) {
            return new JudgeResult(0.0, Submission.Status.FAIL, "", "Empty code", 0);
        }
        
        double score = calculateScore(challenge, code);
        Submission.Status status = score >= 80 ? Submission.Status.PASS : 
                                  score >= 30 ? Submission.Status.PARTIAL : 
                                               Submission.Status.FAIL;
        
        return new JudgeResult(score, status, "Output: " + code.length() + " chars", "", 150);
    }
    
    private double calculateScore(Challenge challenge, String code) {
        return switch (challenge.getType()) {
            case CODE -> code.contains("System.out") ? 80.0 : 50.0;
            case MAPPING -> code.contains("@Entity") ? 90.0 : 40.0;
            case QUERY -> code.contains("SELECT") ? 85.0 : 45.0;
            case BUGFIX -> code.contains("fix") ? 75.0 : 35.0;
        };
    }
    
    @Getter
    public static class JudgeResult {
        private final double score;
        private final Submission.Status status;
        private final String stdout;
        private final String stderr;
        private final int elapsedMs;
        
        public JudgeResult(double score, Submission.Status status, String stdout, String stderr, int elapsedMs) {
            this.score = score;
            this.status = status;
            this.stdout = stdout;
            this.stderr = stderr;
            this.elapsedMs = elapsedMs;
        }
    }
}
