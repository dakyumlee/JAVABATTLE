package com.javabattle.arena.domain.badge;

import com.javabattle.arena.domain.player.Player;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "player_badges")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class PlayerBadge {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player_id")
    private Player player;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "badge_id")
    private Badge badge;
    
    @Column(name = "granted_at")
    private LocalDateTime grantedAt;
    
    public PlayerBadge(Player player, Badge badge) {
        this.player = player;
        this.badge = badge;
        this.grantedAt = LocalDateTime.now();
    }
}
