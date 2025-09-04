package com.javabattle.arena.web.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class SubmissionResult {
    
    private Long submissionId;
    private Double score;
    private String status;
    private String stdout;
    private String stderr;
    private Long elapsedMs;
    
    public static SubmissionResult success(Long submissionId, Double score, String stdout, Long elapsedMs) {
        return new SubmissionResult(submissionId, score, "PASS", stdout, null, elapsedMs);
    }
    
    public static SubmissionResult failure(Long submissionId, Double score, String stderr, Long elapsedMs) {
        return new SubmissionResult(submissionId, score, "FAIL", null, stderr, elapsedMs);
    }
    
    public static SubmissionResult partial(Long submissionId, Double score, String stdout, String stderr, Long elapsedMs) {
        return new SubmissionResult(submissionId, score, "PARTIAL", stdout, stderr, elapsedMs);
    }
}
