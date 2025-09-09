package com.javabattle.arena.repository;

import com.javabattle.arena.model.StudyNote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface StudyNoteRepository extends JpaRepository<StudyNote, Long> {
    List<StudyNote> findByUserIdOrderByCreatedAtDesc(Long userId);
    
    @Query("SELECT s FROM StudyNote s WHERE s.userId = :userId AND s.category = :category ORDER BY s.createdAt DESC")
    List<StudyNote> findByUserIdAndCategory(@Param("userId") Long userId, @Param("category") String category);
    
    @Query("SELECT s FROM StudyNote s WHERE s.userId = :userId AND s.isFavorite = true ORDER BY s.createdAt DESC")
    List<StudyNote> findByUserIdAndIsFavoriteTrue(@Param("userId") Long userId);
    
    @Query("SELECT s FROM StudyNote s WHERE s.userId = :userId AND (s.title LIKE %:keyword% OR s.content LIKE %:keyword% OR s.tags LIKE %:keyword%) ORDER BY s.createdAt DESC")
    List<StudyNote> findByUserIdAndKeyword(@Param("userId") Long userId, @Param("keyword") String keyword);
    
    Long countByUserId(Long userId);
    Long countByUserIdAndIsFavorite(Long userId, Boolean isFavorite);
}
