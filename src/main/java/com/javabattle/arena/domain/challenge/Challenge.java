package com.javabattle.arena.domain.challenge;

import com.javabattle.arena.domain.round.Round;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "challenges")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class Challenge {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "round_id", nullable = false)
    private Round round;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Type type;
    
    @Column(nullable = false, length = 120)
    private String title;
    
    @Column(nullable = false, columnDefinition = "CLOB")
    private String spec; // JSON -> CLOB
    
    @Column(columnDefinition = "CLOB")
    private String expected; // JSON -> CLOB
    
    @Column(nullable = false, columnDefinition = "CLOB")
    private String tests; // JSON -> CLOB
    
    @Column(nullable = false)
    private Integer ord;
    
    public enum Type {
        CODE, BUGFIX, QUERY, MAPPING
    }
}
