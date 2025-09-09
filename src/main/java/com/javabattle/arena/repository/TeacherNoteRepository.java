package com.javabattle.arena.repository;

import com.javabattle.arena.model.TeacherNote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeacherNoteRepository extends JpaRepository<TeacherNote, Long> {
    
    List<TeacherNote> findByTeacherIdOrderByCreatedAtDesc(Long teacherId);
    
    List<TeacherNote> findByTeacherIdAndCategoryOrderByCreatedAtDesc(Long teacherId, String category);
    
    @Query("SELECT n FROM TeacherNote n WHERE n.teacherId = :teacherId AND n.isPinned = true ORDER BY n.updatedAt DESC")
    List<TeacherNote> findPinnedNotesByTeacherId(@Param("teacherId") Long teacherId);
    
    @Query("SELECT n FROM TeacherNote n WHERE n.teacherId = :teacherId ORDER BY n.isPinned DESC, n.updatedAt DESC")
    List<TeacherNote> findAllByTeacherIdOrderByPinnedAndDate(@Param("teacherId") Long teacherId);
    
    long countByTeacherId(Long teacherId);
    
    long countByTeacherIdAndCategory(Long teacherId, String category);
    
    List<TeacherNote> findAllByTeacherIdOrderByCreatedAtDesc(Long teacherId);
}
