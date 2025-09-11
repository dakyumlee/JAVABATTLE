package com.javabattle.arena.repository;

import com.javabattle.arena.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    
    List<Category> findByUserIdOrderByCreatedAtDesc(Long userId);
    
    @Query("SELECT c FROM Category c ORDER BY c.createdAt DESC")
    List<Category> findAllOrderByCreatedAtDesc();
    
    boolean existsByName(String name);
    
    boolean existsByUserIdAndName(Long userId, String name);
    
    long countByUserId(Long userId);
}