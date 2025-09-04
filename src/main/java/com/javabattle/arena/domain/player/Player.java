package com.javabattle.arena.domain.player;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "players")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Player {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 40)
    private String nickname;

    @Column(nullable = false)
    private int exp = 0;

    @Column(name = "rank_tag", nullable = false, length = 24)
    private String rankTag = "NOVICE";

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    public Player(String nickname) {
        this.nickname = nickname;
    }

    public void gainExp(int amount) {
        this.exp += amount;
    }

    public void updateRank(String newRank) {
        this.rankTag = newRank;
    }
}
