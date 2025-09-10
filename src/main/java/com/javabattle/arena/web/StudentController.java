package com.javabattle.arena.web;

import com.javabattle.arena.model.ProblemSubmission;
import com.javabattle.arena.model.QuizSubmission;
import com.javabattle.arena.repository.ProblemSubmissionRepository;
import com.javabattle.arena.repository.QuizSubmissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
            System.out.println("=== 답안 조회 요청 ===");
            System.out.println("요청 사용자 ID: " + userId);
            
            List<ProblemSubmission> submissions = problemSubmissionRepository.findByUserIdOrderBySubmittedAtDesc(userId);
            System.out.println("찾은 답안 수: " + submissions.size());
            
            List<Map<String, Object>> submissionMaps = submissions.stream().map(submission -> {
                Map<String, Object> data = new HashMap<>();
                data.put("id", submission.getId());
                data.put("problemTitle", submission.getProblemTitle());
                data.put("answer", submission.getAnswer());
                data.put("submittedAt", submission.getSubmittedAt().toString());
                data.put("score", submission.getScore());
                data.put("feedback", submission.getFeedback());
                return data;
            }).collect(Collectors.toList());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("submissions", submissionMaps);
            response.put("totalCount", submissions.size());
            
            System.out.println("응답 데이터 크기: " + submissionMaps.size());
            System.out.println("==================");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.out.println("답안 조회 오류: " + e.getMessage());
            e.printStackTrace();
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "답안 조회에 실패했습니다: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/check-notifications")
    public ResponseEntity<Map<String, Object>> checkNotifications(@RequestParam Long userId) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("notifications", new ArrayList<>());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/quiz-submissions")
    public ResponseEntity<Map<String, Object>> getMyQuizSubmissions(@RequestParam Long userId) {
        try {
            List<QuizSubmission> submissions = quizSubmissionRepository.findByUserIdOrderBySubmittedAtDesc(userId);

            List<Map<String, Object>> submissionMaps = submissions.stream().map(submission -> {
                Map<String, Object> data = new HashMap<>();
                data.put("id", submission.getId());
                data.put("quizTitle", submission.getQuizTitle());
                data.put("userAnswer", submission.getUserAnswer());
                data.put("correctAnswer", submission.getCorrectAnswer());
                data.put("isCorrect", submission.getIsCorrect() == 1);
                data.put("submittedAt", submission.getSubmittedAt().toString());
                return data;
            }).collect(Collectors.toList());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("submissions", submissionMaps);
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