package com.javabattle.arena.web;

import com.javabattle.arena.config.JwtUtil;
import com.javabattle.arena.model.TeacherNote;
import com.javabattle.arena.repository.TeacherNoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/teacher")
public class TeacherNoteController {
    
    @Autowired
    private TeacherNoteRepository teacherNoteRepository;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @GetMapping("/notes")
    public ResponseEntity<Map<String, Object>> getAllNotes(HttpServletRequest request) {
        try {
            String token = extractTokenFromRequest(request);
            if (token == null) {
                return ResponseEntity.status(401).body(createErrorResponse("No token provided"));
            }
            
            JwtUtil.UserInfo userInfo = jwtUtil.extractUserInfo(token);
            if (userInfo == null) {
                return ResponseEntity.status(401).body(createErrorResponse("Invalid token"));
            }
            
            List<TeacherNote> notes = teacherNoteRepository.findAllByTeacherIdOrderByCreatedAtDesc(userInfo.getUserId());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("notes", notes);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse("Failed to load notes: " + e.getMessage()));
        }
    }
    
    @PostMapping("/notes")
    public ResponseEntity<Map<String, Object>> createNote(
            @RequestBody Map<String, Object> request, 
            HttpServletRequest httpRequest) {
        try {
            String token = extractTokenFromRequest(httpRequest);
            if (token == null) {
                return ResponseEntity.status(401).body(createErrorResponse("No token provided"));
            }
            
            JwtUtil.UserInfo userInfo = jwtUtil.extractUserInfo(token);
            if (userInfo == null) {
                return ResponseEntity.status(401).body(createErrorResponse("Invalid token"));
            }
            
            String title = (String) request.get("title");
            String content = (String) request.get("content");
            String category = (String) request.get("category");
            
            if (title == null || content == null || category == null) {
                return ResponseEntity.badRequest().body(createErrorResponse("Title, content, and category are required"));
            }
            
            TeacherNote note = new TeacherNote();
            note.setTeacherId(userInfo.getUserId());
            note.setTitle(title);
            note.setContent(content);
            note.setCategory(category);
            note.setIsPinned(false);
            
            TeacherNote savedNote = teacherNoteRepository.save(note);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Note created successfully");
            response.put("noteId", savedNote.getId());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse("Failed to create note: " + e.getMessage()));
        }
    }
    
    @PutMapping("/notes/{noteId}")
    public ResponseEntity<Map<String, Object>> updateNote(
            @PathVariable Long noteId, 
            @RequestBody Map<String, Object> request,
            HttpServletRequest httpRequest) {
        try {
            String token = extractTokenFromRequest(httpRequest);
            if (token == null) {
                return ResponseEntity.status(401).body(createErrorResponse("No token provided"));
            }
            
            JwtUtil.UserInfo userInfo = jwtUtil.extractUserInfo(token);
            if (userInfo == null) {
                return ResponseEntity.status(401).body(createErrorResponse("Invalid token"));
            }
            
            Optional<TeacherNote> noteOpt = teacherNoteRepository.findById(noteId);
            if (!noteOpt.isPresent()) {
                return ResponseEntity.badRequest().body(createErrorResponse("Note not found"));
            }
            
            TeacherNote note = noteOpt.get();
            
            if (!note.getTeacherId().equals(userInfo.getUserId())) {
                return ResponseEntity.status(403).body(createErrorResponse("Access denied"));
            }
            
            if (request.containsKey("title")) {
                note.setTitle((String) request.get("title"));
            }
            if (request.containsKey("content")) {
                note.setContent((String) request.get("content"));
            }
            if (request.containsKey("category")) {
                note.setCategory((String) request.get("category"));
            }
            
            teacherNoteRepository.save(note);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Note updated successfully");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse("Failed to update note: " + e.getMessage()));
        }
    }
    
    @DeleteMapping("/notes/{noteId}")
    public ResponseEntity<Map<String, Object>> deleteNote(
            @PathVariable Long noteId, 
            HttpServletRequest httpRequest) {
        try {
            String token = extractTokenFromRequest(httpRequest);
            if (token == null) {
                return ResponseEntity.status(401).body(createErrorResponse("No token provided"));
            }
            
            JwtUtil.UserInfo userInfo = jwtUtil.extractUserInfo(token);
            if (userInfo == null) {
                return ResponseEntity.status(401).body(createErrorResponse("Invalid token"));
            }
            
            Optional<TeacherNote> noteOpt = teacherNoteRepository.findById(noteId);
            if (!noteOpt.isPresent()) {
                return ResponseEntity.badRequest().body(createErrorResponse("Note not found"));
            }
            
            TeacherNote note = noteOpt.get();
            
            if (!note.getTeacherId().equals(userInfo.getUserId())) {
                return ResponseEntity.status(403).body(createErrorResponse("Access denied"));
            }
            
            teacherNoteRepository.deleteById(noteId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Note deleted successfully");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse("Failed to delete note: " + e.getMessage()));
        }
    }
    
    private String extractTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
    
    private Map<String, Object> createErrorResponse(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", message);
        return response;
    }
}