package com.javabattle.arena.service.player;

import com.javabattle.arena.domain.player.Player;
import com.javabattle.arena.domain.player.PlayerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PlayerService {
    
    private final PlayerRepository playerRepository;
    
    public Player createPlayer(String nickname) {
        if (nickname == null || nickname.trim().isEmpty()) {
            throw new IllegalArgumentException("닉네임을 입력해주세요");
        }
        
        if (nickname.length() > 20) {
            throw new IllegalArgumentException("닉네임은 20자 이하로 입력해주세요");
        }
        
        if (playerRepository.existsByNickname(nickname)) {
            throw new IllegalArgumentException("이미 존재하는 닉네임입니다");
        }
        
        Player newPlayer = new Player(nickname);
        return playerRepository.save(newPlayer);
    }
    
    @Transactional(readOnly = true)
    public Player findByNickname(String nickname) {
        return playerRepository.findByNickname(nickname)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 플레이어입니다"));
    }
    
    @Transactional(readOnly = true)
    public Player findById(Long id) {
        return playerRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("플레이어를 찾을 수 없습니다"));
    }
    
    @Transactional(readOnly = true)
    public List<Player> getTopPlayers(int limit) {
        return playerRepository.findTop20ByOrderByExpDesc();
    }
    
    public void gainExp(Player player, int amount) {
        player.gainExp(amount);
        updateRankByExp(player);
    }
    
    private void updateRankByExp(Player player) {
        int exp = player.getExp();
        String newRank;
        
        if (exp >= 2000) newRank = "DIAMOND";
        else if (exp >= 1000) newRank = "GOLD";
        else if (exp >= 500) newRank = "SILVER";
        else if (exp >= 200) newRank = "BRONZE";
        else newRank = "NOVICE";
        
        if (!player.getRankTag().equals(newRank)) {
            player.updateRank(newRank);
        }
    }
}
