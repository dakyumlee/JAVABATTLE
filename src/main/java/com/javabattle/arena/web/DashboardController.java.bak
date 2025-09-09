package com.javabattle.arena.web;

import com.javabattle.arena.service.StudyNoteService;
import com.javabattle.arena.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
public class DashboardController {
    
    @Autowired
    private StudyNoteService studyNoteService;
    
    @Autowired
    private CategoryService categoryService;
    
    @GetMapping("/api/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboardData(@RequestParam(defaultValue = "1") Long userId) {
        try {
            Long totalNotes = studyNoteService.getTotalNotesCount(userId);
            Long favoriteNotes = studyNoteService.getFavoriteNotesCount(userId);
            Long totalCategories = categoryService.getCategoriesCount(userId);
            
            int progressPercentage = calculateProgress(totalNotes);
            int completedProblems = Math.toIntExact(totalNotes * 2);
            int totalProblems = 150;
            int activityScore = Math.toIntExact(totalNotes + favoriteNotes);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("userId", userId);
            response.put("stats", Map.of(
                "progressPercentage", progressPercentage,
                "totalNotes", totalNotes,
                "favoriteNotes", favoriteNotes,
                "totalCategories", totalCategories,
                "completedProblems", completedProblems,
                "totalProblems", totalProblems,
                "activityScore", activityScore
            ));
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "대시보드 데이터 조회 중 오류가 발생했습니다.");
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    
    private int calculateProgress(Long totalNotes) {
        if (totalNotes == 0) return 0;
        if (totalNotes >= 50) return 100;
        return Math.toIntExact((totalNotes * 100) / 50);
    }
}
