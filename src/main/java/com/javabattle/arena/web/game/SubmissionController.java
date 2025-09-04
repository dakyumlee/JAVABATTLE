package com.javabattle.arena.web.game;

import com.javabattle.arena.service.game.SubmissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class SubmissionController {
    
    private final SubmissionService submissionService;
    
    @PostMapping("/submissions")
    public SubmissionService.SubmissionResult submitCode(@RequestBody SubmissionRequest request) {
        return submissionService.submitCode(
            request.playerId(), 
            request.challengeId(), 
            request.code()
        );
    }
    
    public record SubmissionRequest(
        Long playerId,
        Long challengeId, 
        String code
    ) {}
}
