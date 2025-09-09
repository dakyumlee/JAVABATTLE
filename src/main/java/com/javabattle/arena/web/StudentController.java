package com.javabattle.arena.web;

import com.javabattle.arena.model.ProblemSubmission;
import com.javabattle.arena.model.QuizSubmission;
import com.javabattle.arena.repository.ProblemSubmissionRepository;
import com.javabattle.arena.repository.QuizSubmissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/student")
public class StudentController {
    
    @Autowired
    private ProblemSubmissionRepository problemSubmissionRepository;
    
    @Autowired
    private QuizSubmissionRepository quizSubmissionRepository;
    
    @GetMapping("/submissions")
    public ResponseEntity<Map<String, Object>> getMySubmissions(@RequestParam Long userId) {
        try {
            List<ProblemSubmission> submissions = problemSubmissionRepository.findByUserIdOrderBySubmittedAtDesc(userId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("submissions", submissions);
            response.put("totalCount", submissions.size());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "답안 조회에 실패했습니다: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @GetMapping("/quiz-submissions")
    public ResponseEntity<Map<String, Object>> getMyQuizSubmissions(@RequestParam Long userId) {
        try {
            List<QuizSubmission> submissions = quizSubmissionRepository.findByUserIdOrderBySubmittedAtDesc(userId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("submissions", submissions);
            response.put("totalCount", submissions.size());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "퀴즈 답안 조회에 실패했습니다: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("StudentController 작동 중");
    }
}