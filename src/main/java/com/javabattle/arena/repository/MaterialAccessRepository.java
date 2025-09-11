package com.javabattle.arena.repository;

import com.javabattle.arena.model.MaterialAccess;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;

public interface MaterialAccessRepository extends JpaRepository<MaterialAccess, Long> {
    
    List<MaterialAccess> findByStudentIdAndAccessTimeBetween(Long studentId, LocalDateTime start, LocalDateTime end);
    
    List<MaterialAccess> findByMaterialIdAndAccessTimeBetween(Long materialId, LocalDateTime start, LocalDateTime end);
    
    @Query("SELECT m.materialName, COUNT(m) FROM MaterialAccess m WHERE m.accessTime >= :start GROUP BY m.materialName ORDER BY COUNT(m) DESC")
    List<Object[]> getPopularMaterialsSince(@Param("start") LocalDateTime start);
    
    @Query("SELECT COUNT(DISTINCT m.studentId) FROM MaterialAccess m WHERE m.materialId = :materialId AND m.accessTime >= :start")
    Long countUniqueAccessorsSince(@Param("materialId") Long materialId, @Param("start") LocalDateTime start);
    
    @Query("SELECT AVG(m.durationSeconds) FROM MaterialAccess m WHERE m.materialId = :materialId AND m.durationSeconds IS NOT NULL")
    Double getAverageViewDuration(@Param("materialId") Long materialId);
    
    Long countByAccessTimeAfter(LocalDateTime start);
    
    Long countByAccessTimeBetween(LocalDateTime start, LocalDateTime end);
    
    @Query("SELECT MAX(m.accessTime) FROM MaterialAccess m WHERE m.studentId = :studentId")
    LocalDateTime findTopByStudentIdOrderByAccessTimeDesc(@Param("studentId") Long studentId);
}