package com.javabattle.arena.web;

import com.javabattle.arena.model.StudyNote;
import com.javabattle.arena.model.Category;
import com.javabattle.arena.service.StudyNoteService;
import com.javabattle.arena.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
public class StudyNoteController {
    
    @Autowired
    private StudyNoteService studyNoteService;
    
    @Autowired
    private CategoryService categoryService;
    
    @GetMapping("/api/study-notes")
    public ResponseEntity<Map<String, Object>> getAllNotes(@RequestParam(defaultValue = "1") Long userId) {
        try {
            List<StudyNote> notes = studyNoteService.getAllNotesByUserId(userId);
            List<Category> categories = categoryService.getAllCategoriesByUserId(userId);
            Long totalCount = studyNoteService.getTotalNotesCount(userId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("notes", notes);
            response.put("categories", categories);
            response.put("totalCount", totalCount);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "노트 조회 중 오류가 발생했습니다.");
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    
    @GetMapping("/api/categories")
    public ResponseEntity<Map<String, Object>> getCategories(@RequestParam(defaultValue = "1") Long userId) {
        try {
            List<Category> categories = categoryService.getAllCategoriesByUserId(userId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("categories", categories);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "카테고리 조회 중 오류가 발생했습니다.");
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    
    @PostMapping("/api/categories")
    public ResponseEntity<Map<String, Object>> createCategory(@RequestBody Map<String, Object> request) {
        try {
            Long userId = Long.valueOf(request.get("userId").toString());
            String name = (String) request.get("name");
            String color = (String) request.get("color");
            
            Category category = categoryService.createCategory(userId, name, color);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("category", category);
            response.put("message", "카테고리가 성공적으로 생성되었습니다.");
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "카테고리 생성 중 오류가 발생했습니다.");
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    
    @PutMapping("/api/categories/{id}")
    public ResponseEntity<Map<String, Object>> updateCategory(
            @PathVariable Long id,
            @RequestBody Map<String, Object> request) {
        try {
            String name = (String) request.get("name");
            String color = (String) request.get("color");
            
            Category category = categoryService.updateCategory(id, name, color);
            
            Map<String, Object> response = new HashMap<>();
            if (category != null) {
                response.put("success", true);
                response.put("category", category);
                response.put("message", "카테고리가 성공적으로 수정되었습니다.");
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "카테고리를 찾을 수 없습니다.");
                return ResponseEntity.notFound().build();
            }
        } catch (RuntimeException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "카테고리 수정 중 오류가 발생했습니다.");
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    
    @DeleteMapping("/api/categories/{id}")
    public ResponseEntity<Map<String, Object>> deleteCategory(@PathVariable Long id) {
        try {
            boolean success = categoryService.deleteCategory(id);
            
            Map<String, Object> response = new HashMap<>();
            if (success) {
                response.put("success", true);
                response.put("message", "카테고리가 성공적으로 삭제되었습니다.");
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "카테고리를 찾을 수 없습니다.");
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "카테고리 삭제 중 오류가 발생했습니다.");
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    
    @GetMapping("/api/study-notes/category/{category}")
    public ResponseEntity<Map<String, Object>> getNotesByCategory(
            @PathVariable String category,
            @RequestParam(defaultValue = "1") Long userId) {
        try {
            List<StudyNote> notes = studyNoteService.getNotesByCategory(userId, category);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("notes", notes);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "카테고리별 노트 조회 중 오류가 발생했습니다.");
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    
    @GetMapping("/api/study-notes/favorites")
    public ResponseEntity<Map<String, Object>> getFavoriteNotes(@RequestParam(defaultValue = "1") Long userId) {
        try {
            List<StudyNote> notes = studyNoteService.getFavoriteNotes(userId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("notes", notes);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "즐겨찾기 노트 조회 중 오류가 발생했습니다.");
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    
    @GetMapping("/api/study-notes/search")
    public ResponseEntity<Map<String, Object>> searchNotes(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "1") Long userId) {
        try {
            List<StudyNote> notes = studyNoteService.searchNotes(userId, keyword);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("notes", notes);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "노트 검색 중 오류가 발생했습니다.");
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    
    @PostMapping("/api/study-notes")
    public ResponseEntity<Map<String, Object>> createNote(@RequestBody Map<String, Object> request) {
        try {
            Long userId = Long.valueOf(request.get("userId").toString());
            String title = (String) request.get("title");
            String content = (String) request.get("content");
            String category = (String) request.get("category");
            String tags = (String) request.get("tags");
            Integer difficultyLevel = request.get("difficultyLevel") != null ? 
                Integer.valueOf(request.get("difficultyLevel").toString()) : null;
            
            StudyNote note = studyNoteService.createNote(userId, title, content, category, tags, difficultyLevel);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("note", note);
            response.put("message", "노트가 성공적으로 생성되었습니다.");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "노트 생성 중 오류가 발생했습니다.");
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    
    @PutMapping("/api/study-notes/{id}")
    public ResponseEntity<Map<String, Object>> updateNote(
            @PathVariable Long id,
            @RequestBody Map<String, Object> request) {
        try {
            String title = (String) request.get("title");
            String content = (String) request.get("content");
            String category = (String) request.get("category");
            String tags = (String) request.get("tags");
            Integer difficultyLevel = request.get("difficultyLevel") != null ? 
                Integer.valueOf(request.get("difficultyLevel").toString()) : null;
            
            StudyNote note = studyNoteService.updateNote(id, title, content, category, tags, difficultyLevel);
            
            Map<String, Object> response = new HashMap<>();
            if (note != null) {
                response.put("success", true);
                response.put("note", note);
                response.put("message", "노트가 성공적으로 수정되었습니다.");
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "노트를 찾을 수 없습니다.");
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "노트 수정 중 오류가 발생했습니다.");
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    
    @PostMapping("/api/study-notes/{id}/favorite")
    public ResponseEntity<Map<String, Object>> toggleFavorite(@PathVariable Long id) {
        try {
            boolean success = studyNoteService.toggleFavorite(id);
            
            Map<String, Object> response = new HashMap<>();
            if (success) {
                response.put("success", true);
                response.put("message", "즐겨찾기가 토글되었습니다.");
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "노트를 찾을 수 없습니다.");
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "즐겨찾기 토글 중 오류가 발생했습니다.");
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    
    @DeleteMapping("/api/study-notes/{id}")
    public ResponseEntity<Map<String, Object>> deleteNote(@PathVariable Long id) {
        try {
            boolean success = studyNoteService.deleteNote(id);
            
            Map<String, Object> response = new HashMap<>();
            if (success) {
                response.put("success", true);
                response.put("message", "노트가 성공적으로 삭제되었습니다.");
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "노트를 찾을 수 없습니다.");
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "노트 삭제 중 오류가 발생했습니다.");
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
}