package com.javabattle.arena.web.ranking;

import com.javabattle.arena.domain.player.Player;
import com.javabattle.arena.service.player.PlayerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class RankingController {
    
    private final PlayerService playerService;
    
    @GetMapping("/leaderboard")
    public List<Player> getLeaderboard() {
        return playerService.getTopPlayers(20);
    }
}
