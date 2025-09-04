package com.javabattle.arena.web.game;

import com.javabattle.arena.domain.challenge.Challenge;
import com.javabattle.arena.domain.round.Round;
import com.javabattle.arena.service.game.GameService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class GameController {
    
    private final GameService gameService;
    
    @GetMapping("/rounds")
    public List<Round> getRounds() {
        return gameService.getAllRounds();
    }
    
    @GetMapping("/challenges")
    public List<Challenge> getChallenges(@RequestParam Long round) {
        return gameService.getChallengesByRound(round);
    }
}
