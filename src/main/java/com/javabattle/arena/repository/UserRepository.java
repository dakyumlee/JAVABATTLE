package com.javabattle.arena.repository;

import com.javabattle.arena.model.User;
import com.javabattle.arena.model.User.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    
    Long countByRole(UserRole role);
    List<User> findByRole(UserRole role);
}