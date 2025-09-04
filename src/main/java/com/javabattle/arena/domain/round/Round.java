package com.javabattle.arena.domain.round;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "rounds")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Round {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private int ord;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false)
    private boolean boss = false;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    public Round(int ord, String title, boolean boss) {
        this.ord = ord;
        this.title = title;
        this.boss = boss;
    }
}
