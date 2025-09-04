package com.javabattle.arena.service.game;

import com.javabattle.arena.domain.challenge.Challenge;
import com.javabattle.arena.domain.challenge.ChallengeRepository;
import com.javabattle.arena.domain.round.Round;
import com.javabattle.arena.domain.round.RoundRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GameService {
    
    private final RoundRepository roundRepository;
    private final ChallengeRepository challengeRepository;
    
    public List<Round> getAllRounds() {
        return roundRepository.findAllByOrderByOrd();
    }
    
    public List<Challenge> getChallengesByRound(Long roundId) {
        Round round = roundRepository.findById(roundId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 라운드입니다"));
        
        return challengeRepository.findByRoundIdOrderByOrd(roundId);
    }
    
    public Challenge getChallenge(Long challengeId) {
        return challengeRepository.findById(challengeId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 문제입니다"));
    }
    
    public Round getRound(Long roundId) {
        return roundRepository.findById(roundId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 라운드입니다"));
    }
}
