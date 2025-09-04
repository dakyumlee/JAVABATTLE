package com.javabattle.arena.domain.submission;

import com.javabattle.arena.domain.challenge.Challenge;
import com.javabattle.arena.domain.player.Player;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "submissions")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class Submission {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player_id", nullable = false)
    private Player player;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "challenge_id", nullable = false)
    private Challenge challenge;
    
    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal score;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;
    
    @Column(columnDefinition = "CLOB")
    private String stdout;
    
    @Column(columnDefinition = "CLOB")
    private String stderr;
    
    @Column(name = "elapsed_ms", nullable = false)
    private Integer elapsedMs;
    
    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;
    
    public enum Status {
        PASS, FAIL, PARTIAL
    }
    
    public Submission(Player player, Challenge challenge, BigDecimal score, Status status) {
        this.player = player;
        this.challenge = challenge;
        this.score = score;
        this.status = status;
        this.createdAt = OffsetDateTime.now();
    }
    
    public void updateResult(BigDecimal score, Status status, String stdout, String stderr, Integer elapsedMs) {
        this.score = score;
        this.status = status;
        this.stdout = stdout;
        this.stderr = stderr;
        this.elapsedMs = elapsedMs;
    }
}
