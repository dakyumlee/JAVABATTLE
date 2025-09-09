package com.javabattle.arena.service;

import com.javabattle.arena.model.StudyNote;
import com.javabattle.arena.repository.StudyNoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class StudyNoteService {
    
    @Autowired
    private StudyNoteRepository studyNoteRepository;
    
    public List<StudyNote> getAllNotesByUserId(Long userId) {
        return studyNoteRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }
    
    public List<StudyNote> getNotesByCategory(Long userId, String category) {
        return studyNoteRepository.findByUserIdAndCategory(userId, category);
    }
    
    public List<StudyNote> getFavoriteNotes(Long userId) {
        return studyNoteRepository.findByUserIdAndIsFavoriteTrue(userId);
    }
    
    public List<StudyNote> searchNotes(Long userId, String keyword) {
        return studyNoteRepository.findByUserIdAndKeyword(userId, keyword);
    }
    
    public StudyNote createNote(Long userId, String title, String content, String category, String tags, Integer difficultyLevel) {
        StudyNote note = new StudyNote();
        note.setUserId(userId);
        note.setTitle(title);
        note.setContent(content);
        note.setCategory(category);
        note.setTags(tags);
        note.setDifficultyLevel(difficultyLevel);
        note.setIsFavorite(false);
        note.setCreatedAt(LocalDateTime.now());
        note.setUpdatedAt(LocalDateTime.now());
        
        return studyNoteRepository.save(note);
    }
    
    public StudyNote updateNote(Long id, String title, String content, String category, String tags, Integer difficultyLevel) {
        StudyNote note = studyNoteRepository.findById(id).orElse(null);
        if (note != null) {
            note.setTitle(title);
            note.setContent(content);
            note.setCategory(category);
            note.setTags(tags);
            note.setDifficultyLevel(difficultyLevel);
            note.setUpdatedAt(LocalDateTime.now());
            return studyNoteRepository.save(note);
        }
        return null;
    }
    
    public boolean toggleFavorite(Long id) {
        StudyNote note = studyNoteRepository.findById(id).orElse(null);
        if (note != null) {
            note.setIsFavorite(!note.getIsFavorite());
            note.setUpdatedAt(LocalDateTime.now());
            studyNoteRepository.save(note);
            return true;
        }
        return false;
    }
    
    public boolean deleteNote(Long id) {
        if (studyNoteRepository.existsById(id)) {
            studyNoteRepository.deleteById(id);
            return true;
        }
        return false;
    }
    
    public Long getTotalNotesCount(Long userId) {
        return studyNoteRepository.countByUserId(userId);
    }
    
    public Long getFavoriteNotesCount(Long userId) {
        return studyNoteRepository.countByUserIdAndIsFavorite(userId, true);
    }
}
