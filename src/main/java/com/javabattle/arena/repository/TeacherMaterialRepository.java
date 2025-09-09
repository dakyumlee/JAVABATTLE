package com.javabattle.arena.repository;

import com.javabattle.arena.model.TeacherMaterial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TeacherMaterialRepository extends JpaRepository<TeacherMaterial, Long> {
    
    @Query("SELECT t FROM TeacherMaterial t WHERE t.uploadedBy = :teacherId ORDER BY t.createdAt DESC")
    List<TeacherMaterial> findByTeacherId(@Param("teacherId") Long teacherId);
    
    @Query("SELECT t FROM TeacherMaterial t ORDER BY t.createdAt DESC")
    List<TeacherMaterial> findAllOrderByCreatedAtDesc();
    
    @Query("SELECT t FROM TeacherMaterial t WHERE t.fileType = :fileType ORDER BY t.createdAt DESC")
    List<TeacherMaterial> findByFileType(@Param("fileType") String fileType);
}