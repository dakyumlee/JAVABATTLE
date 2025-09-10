package com.javabattle.arena.repository;

import com.javabattle.arena.model.TeacherMaterial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeacherMaterialRepository extends JpaRepository<TeacherMaterial, Long> {
    
    @Query("SELECT t FROM TeacherMaterial t WHERE t.teacherId = :teacherId ORDER BY t.createdAt DESC")
    List<TeacherMaterial> findByTeacherId(@Param("teacherId") Long teacherId);
    
    @Query("SELECT t FROM TeacherMaterial t WHERE t.isShared = true ORDER BY t.createdAt DESC")
    List<TeacherMaterial> findSharedMaterials();
    
    @Query("SELECT t FROM TeacherMaterial t ORDER BY t.createdAt DESC")
    List<TeacherMaterial> findAllOrderByCreatedAtDesc();
    
    @Query("SELECT t FROM TeacherMaterial t WHERE t.category = :category ORDER BY t.createdAt DESC")
    List<TeacherMaterial> findByCategory(@Param("category") String category);
    
    @Query("SELECT t FROM TeacherMaterial t WHERE t.materialType = :materialType ORDER BY t.createdAt DESC")
    List<TeacherMaterial> findByMaterialType(@Param("materialType") String materialType);
    
    @Query("SELECT COUNT(t) FROM TeacherMaterial t WHERE t.teacherId = :teacherId")
    long countByTeacherId(@Param("teacherId") Long teacherId);
    
    @Query("SELECT COUNT(t) FROM TeacherMaterial t WHERE t.isShared = true")
    long countSharedMaterials();
    
    @Query("SELECT t FROM TeacherMaterial t WHERE t.materialType = :fileType ORDER BY t.createdAt DESC")
    List<TeacherMaterial> findByFileType(@Param("fileType") String fileType);
    
    @Query("SELECT t FROM TeacherMaterial t WHERE t.teacherId = :teacherId AND t.materialType = :fileType ORDER BY t.createdAt DESC")
    List<TeacherMaterial> findByTeacherIdAndFileType(@Param("teacherId") Long teacherId, @Param("fileType") String fileType);
}