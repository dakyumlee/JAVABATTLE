package com.javabattle.arena.domain.player;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface PlayerRepository extends JpaRepository<Player, Long> {
    Optional<Player> findByNickname(String nickname);
    boolean existsByNickname(String nickname);
    List<Player> findTop20ByOrderByExpDesc();
}
