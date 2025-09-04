package com.javabattle.arena.service.game;

import com.javabattle.arena.domain.badge.Badge;
import com.javabattle.arena.domain.badge.BadgeRepository;
import com.javabattle.arena.domain.badge.PlayerBadge;
import com.javabattle.arena.domain.badge.PlayerBadgeRepository;
import com.javabattle.arena.domain.player.Player;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class BadgeService {
    
    private final BadgeRepository badgeRepository;
    private final PlayerBadgeRepository playerBadgeRepository;
    
    public void grantBadge(Player player, String badgeCode) {
        Badge badge = badgeRepository.findByCode(badgeCode).orElse(null);
        if (badge == null) return;
        
        boolean alreadyHas = playerBadgeRepository.existsByPlayerAndBadge(player, badge);
        if (alreadyHas) return;
        
        PlayerBadge playerBadge = new PlayerBadge(player, badge);
        playerBadgeRepository.save(playerBadge);
    }
    
    @Transactional(readOnly = true)
    public List<PlayerBadge> getPlayerBadges(Player player) {
        return playerBadgeRepository.findByPlayerOrderByGrantedAtDesc(player);
    }
    
    @Transactional(readOnly = true)
    public List<Badge> getAllBadges() {
        return badgeRepository.findAll();
    }
}
