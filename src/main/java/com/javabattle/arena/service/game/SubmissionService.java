package com.javabattle.arena.service.game;

import com.javabattle.arena.domain.challenge.Challenge;
import com.javabattle.arena.domain.player.Player;
import com.javabattle.arena.domain.submission.Submission;
import com.javabattle.arena.domain.submission.SubmissionRepository;
import com.javabattle.arena.judge.executor.JudgeService;
import com.javabattle.arena.service.player.PlayerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class SubmissionService {
    
    private final SubmissionRepository submissionRepository;
    private final PlayerService playerService;
    private final GameService gameService;
    private final JudgeService judgeService;
    private final BadgeService badgeService;
    
    public SubmissionResult submitCode(Long playerId, Long challengeId, String code) {
        if (code == null || code.trim().isEmpty()) {
            throw new IllegalArgumentException("코드를 입력해주세요");
        }
        
        Player player = playerService.findById(playerId);
        Challenge challenge = gameService.getChallenge(challengeId);
        
        JudgeService.JudgeResult judgeResult = judgeService.judge(challenge, code);
        
        Submission submission = new Submission(player, challenge, 
            BigDecimal.valueOf(judgeResult.getScore()), judgeResult.getStatus());
        
        submission.updateResult(
            BigDecimal.valueOf(judgeResult.getScore()),
            judgeResult.getStatus(),
            judgeResult.getStdout(),
            judgeResult.getStderr(),
            judgeResult.getElapsedMs()
        );
        
        submission = submissionRepository.save(submission);
        
        if (judgeResult.getStatus() == Submission.Status.PASS) {
            int expGain = calculateExpGain(challenge, judgeResult.getScore());
            playerService.gainExp(player, expGain);
            
            checkAndGrantBadges(player, challenge);
        }
        
        return new SubmissionResult(submission.getId(), submission.getScore(), 
                                    submission.getStatus(), submission.getStdout(), 
                                    submission.getStderr());
    }
    
    @Transactional(readOnly = true)
    public List<Submission> getPlayerSubmissions(Long playerId) {
        return submissionRepository.findByPlayerIdOrderByCreatedAtDesc(playerId);
    }
    
    private int calculateExpGain(Challenge challenge, double score) {
        int baseExp = switch (challenge.getType()) {
            case CODE -> 20;
            case BUGFIX -> 15;
            case QUERY -> 25;
            case MAPPING -> 30;
        };
        
        return (int) (baseExp * (score / 100.0));
    }
    
    private void checkAndGrantBadges(Player player, Challenge challenge) {
        Long roundId = challenge.getRound().getId();
        
        switch (roundId.intValue()) {
            case 1 -> badgeService.grantBadge(player, "SYNTAX_KILLER");
            case 6 -> badgeService.grantBadge(player, "ENTITY_MASTER");
            case 7 -> badgeService.grantBadge(player, "NPLUS1_SLAYER");
            case 10 -> badgeService.grantBadge(player, "TX_DRAGON_SLAYER");
        }
    }
    
    public static class SubmissionResult {
        public Long submissionId;
        public BigDecimal score;
        public Submission.Status status;
        public String stdout;
        public String stderr;
        
        public SubmissionResult(Long submissionId, BigDecimal score, 
                                Submission.Status status, String stdout, String stderr) {
            this.submissionId = submissionId;
            this.score = score;
            this.status = status;
            this.stdout = stdout;
            this.stderr = stderr;
        }
    }
}
