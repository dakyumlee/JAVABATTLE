package com.javabattle.arena.domain.badge;

import com.javabattle.arena.domain.player.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PlayerBadgeRepository extends JpaRepository<PlayerBadge, Long> {
    
    List<PlayerBadge> findByPlayer(Player player);
    
    List<PlayerBadge> findByPlayerOrderByGrantedAtDesc(Player player);
    
    @Query("SELECT pb FROM PlayerBadge pb JOIN FETCH pb.badge WHERE pb.player = :player ORDER BY pb.grantedAt DESC")
    List<PlayerBadge> findByPlayerWithBadgeOrderByGrantedAtDesc(@Param("player") Player player);
    
    boolean existsByPlayerAndBadge(Player player, Badge badge);
}
