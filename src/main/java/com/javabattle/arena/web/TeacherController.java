package com.javabattle.arena.web;

import com.javabattle.arena.model.ActiveSession;
import com.javabattle.arena.model.ProblemSubmission;
import com.javabattle.arena.model.QuizSubmission;
import com.javabattle.arena.model.User;
import com.javabattle.arena.model.TeacherNote;
import com.javabattle.arena.repository.ProblemSubmissionRepository;
import com.javabattle.arena.repository.QuizSubmissionRepository;
import com.javabattle.arena.repository.UserRepository;
import com.javabattle.arena.repository.TeacherNoteRepository;
import com.javabattle.arena.service.SessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class TeacherController {
    
    @Autowired
    private SessionService sessionService;
    
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    
    @Autowired
    private ProblemSubmissionRepository problemSubmissionRepository;
    
    @Autowired
    private QuizSubmissionRepository quizSubmissionRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private TeacherNoteRepository teacherNoteRepository;
    
    @GetMapping("/api/teacher/active-students")
    public ResponseEntity<List<Map<String, Object>>> getActiveStudents() {
        try {
            List<ActiveSession> sessions = sessionService.getActiveSessions();
            
            List<Map<String, Object>> result = sessions.stream()
                .filter(session -> session.getIsActive() != null && session.getIsActive())
                .map(session -> {
                    Map<String, Object> studentData = new HashMap<>();
                    studentData.put("userId", session.getUserId());
                    studentData.put("sessionId", session.getSessionId());
                    studentData.put("currentPage", session.getCurrentPage());
                    studentData.put("lastActivity", session.getLastActivity());
                    studentData.put("isCoding", session.getIsCoding());
                    studentData.put("codeLength", session.getCurrentCode() != null ? session.getCurrentCode().length() : 0);
                    studentData.put("startTime", session.getStartTime());
                    return studentData;
                }).collect(Collectors.toList());
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PostMapping("/api/teacher/send-hint")
    public ResponseEntity<Map<String, Object>> sendHint(@RequestBody Map<String, Object> request) {
        try {
            Long studentId = Long.valueOf(request.get("studentId").toString());
            String message = (String) request.get("message");
            
            Map<String, Object> hintData = new HashMap<>();
            hintData.put("type", "HINT");
            hintData.put("message", message);
            hintData.put("from", "teacher");
            hintData.put("timestamp", LocalDateTime.now());
            
            messagingTemplate.convertAndSendToUser(
                studentId.toString(), 
                "/queue/hints", 
                hintData
            );
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Hint sent successfully");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to send hint: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @PostMapping("/api/teacher/global-hint")
    public ResponseEntity<Map<String, Object>> sendGlobalHint(@RequestBody Map<String, Object> request) {
        try {
            String message = (String) request.get("message");
            
            Map<String, Object> hintData = new HashMap<>();
            hintData.put("type", "GLOBAL_HINT");
            hintData.put("message", message);
            hintData.put("from", "teacher");
            hintData.put("timestamp", LocalDateTime.now());
            
            messagingTemplate.convertAndSend("/topic/global-hints", hintData);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Global hint sent successfully");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to send global hint: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @PostMapping("/api/teacher/create-problem")
    public ResponseEntity<Map<String, Object>> createProblem(@RequestBody Map<String, Object> request) {
        try {
            String title = (String) request.get("title");
            String description = (String) request.get("description");
            
            Map<String, Object> problemData = new HashMap<>();
            problemData.put("type", "NEW_PROBLEM");
            problemData.put("title", title);
            problemData.put("description", description);
            problemData.put("from", "teacher");
            problemData.put("timestamp", LocalDateTime.now());
            
            messagingTemplate.convertAndSend("/topic/teacher-announcements", problemData);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Problem created and announced successfully");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to create problem: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @PostMapping("/api/teacher/quick-quiz")
    public ResponseEntity<Map<String, Object>> sendQuickQuiz(@RequestBody Map<String, Object> request) {
        try {
            String title = (String) request.get("title");
            String question = (String) request.get("question");
            List<String> options = (List<String>) request.get("options");
            Integer correctAnswer = (Integer) request.get("correctAnswer");
            
            Map<String, Object> quizData = new HashMap<>();
            quizData.put("type", "QUICK_QUIZ");
            quizData.put("title", title);
            quizData.put("question", question);
            quizData.put("options", options);
            quizData.put("correctAnswer", correctAnswer);
            quizData.put("from", "teacher");
            quizData.put("timestamp", LocalDateTime.now());
            quizData.put("duration", 60);
            
            messagingTemplate.convertAndSend("/topic/quiz-broadcast", quizData);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Quiz sent successfully");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to send quiz: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @PostMapping("/api/student/submit-answer")
    public ResponseEntity<Map<String, Object>> submitAnswer(@RequestBody Map<String, Object> request) {
        try {
            Object userIdObj = request.get("userId");
            String answer = (String) request.get("answer");
            String timestamp = (String) request.get("timestamp");
            String problemTitle = (String) request.get("problemTitle");
            
            Long userId = null;
            if (userIdObj instanceof Integer) {
                userId = ((Integer) userIdObj).longValue();
            } else if (userIdObj instanceof Long) {
                userId = (Long) userIdObj;
            } else if (userIdObj instanceof String) {
                userId = Long.valueOf((String) userIdObj);
            }
            
            if (userId == null || answer == null || answer.trim().isEmpty()) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "사용자 ID와 답안은 필수입니다.");
                return ResponseEntity.badRequest().body(response);
            }
            
            ProblemSubmission submission = new ProblemSubmission(userId, problemTitle, answer);
            problemSubmissionRepository.save(submission);
            
            System.out.println("=== 답안 제출 수신 및 저장 ===");
            System.out.println("학생 ID: " + userId);
            System.out.println("문제 제목: " + problemTitle);
            System.out.println("답안 내용: " + answer);
            System.out.println("제출 시간: " + submission.getSubmittedAt());
            System.out.println("DB 저장 ID: " + submission.getId());
            System.out.println("========================");
            
            Map<String, Object> answerNotification = new HashMap<>();
            answerNotification.put("type", "ANSWER_SUBMITTED");
            answerNotification.put("userId", userId);
            answerNotification.put("answer", answer);
            answerNotification.put("problemTitle", problemTitle);
            answerNotification.put("timestamp", submission.getSubmittedAt());
            answerNotification.put("submissionId", submission.getId());
            
            messagingTemplate.convertAndSend("/topic/teacher-notifications", answerNotification);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "답안이 성공적으로 제출되었습니다.");
            response.put("submissionId", submission.getId());
            response.put("submittedAt", submission.getSubmittedAt());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "답안 제출 중 오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @PostMapping("/api/student/submit-quiz")
    public ResponseEntity<Map<String, Object>> submitQuiz(@RequestBody Map<String, Object> request) {
        try {
            Object userIdObj = request.get("userId");
            String quizTitle = (String) request.get("quizTitle");
            String question = (String) request.get("question");
            Integer userAnswer = (Integer) request.get("userAnswer");
            Integer correctAnswer = (Integer) request.get("correctAnswer");
            
            Long userId = null;
            if (userIdObj instanceof Integer) {
                userId = ((Integer) userIdObj).longValue();
            } else if (userIdObj instanceof Long) {
                userId = (Long) userIdObj;
            } else if (userIdObj instanceof String) {
                userId = Long.valueOf((String) userIdObj);
            }
            
            if (userId == null || userAnswer == null || correctAnswer == null) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "필수 데이터가 누락되었습니다.");
                return ResponseEntity.badRequest().body(response);
            }
            
            QuizSubmission submission = new QuizSubmission(userId, quizTitle, question, userAnswer, correctAnswer);
            quizSubmissionRepository.save(submission);
            
            System.out.println("=== 퀴즈 답안 제출 수신 및 저장 ===");
            System.out.println("학생 ID: " + userId);
            System.out.println("퀴즈 제목: " + quizTitle);
            System.out.println("학생 답안: " + (userAnswer + 1) + "번");
            System.out.println("정답: " + (correctAnswer + 1) + "번");
            System.out.println("정답 여부: " + submission.getIsCorrect());
            System.out.println("DB 저장 ID: " + submission.getId());
            System.out.println("========================");
            
            Map<String, Object> quizNotification = new HashMap<>();
            quizNotification.put("type", "QUIZ_SUBMITTED");
            quizNotification.put("userId", userId);
            quizNotification.put("quizTitle", quizTitle);
            quizNotification.put("userAnswer", userAnswer);
            quizNotification.put("correctAnswer", correctAnswer);
            quizNotification.put("isCorrect", submission.getIsCorrect());
            quizNotification.put("timestamp", submission.getSubmittedAt());
            quizNotification.put("submissionId", submission.getId());
            
            messagingTemplate.convertAndSend("/topic/teacher-notifications", quizNotification);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("isCorrect", submission.getIsCorrect());
            response.put("message", submission.getIsCorrect() == 1 ? "정답입니다!" : "틀렸습니다.");
            response.put("submissionId", submission.getId());
            response.put("submittedAt", submission.getSubmittedAt());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "퀴즈 답안 제출 중 오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @GetMapping("/api/teacher/submissions")
    public ResponseEntity<Map<String, Object>> getSubmissions() {
        try {
            List<ProblemSubmission> submissions = problemSubmissionRepository.findAllOrderBySubmittedAtDesc();
            
            List<Map<String, Object>> result = submissions.stream().map(submission -> {
                Map<String, Object> data = new HashMap<>();
                data.put("id", submission.getId());
                data.put("userId", submission.getUserId());
                data.put("problemTitle", submission.getProblemTitle());
                data.put("answer", submission.getAnswer());
                data.put("submittedAt", submission.getSubmittedAt());
                data.put("score", submission.getScore());
                data.put("feedback", submission.getFeedback());
                return data;
            }).collect(Collectors.toList());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("submissions", result);
            response.put("totalCount", result.size());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "답안 조회 중 오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @GetMapping("/api/teacher/quiz-submissions")
    public ResponseEntity<Map<String, Object>> getQuizSubmissions() {
        try {
            List<QuizSubmission> submissions = quizSubmissionRepository.findAllOrderBySubmittedAtDesc();
            
            List<Map<String, Object>> result = submissions.stream().map(submission -> {
                Map<String, Object> data = new HashMap<>();
                data.put("id", submission.getId());
                data.put("userId", submission.getUserId());
                data.put("quizTitle", submission.getQuizTitle());
                data.put("question", submission.getQuestion());
                data.put("userAnswer", submission.getUserAnswer());
                data.put("correctAnswer", submission.getCorrectAnswer());
                data.put("isCorrect", submission.getIsCorrect());
                data.put("submittedAt", submission.getSubmittedAt());
                return data;
            }).collect(Collectors.toList());
            
            long correctCount = quizSubmissionRepository.countCorrectAnswers();
            long totalCount = quizSubmissionRepository.countTotalAnswers();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("submissions", result);
            response.put("totalCount", totalCount);
            response.put("correctCount", correctCount);
            response.put("accuracy", totalCount > 0 ? (double) correctCount / totalCount * 100 : 0);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "퀴즈 답안 조회 중 오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @PostMapping("/api/teacher/submissions/{id}/score")
    public ResponseEntity<Map<String, Object>> scoreSubmission(
            @PathVariable Long id,
            @RequestBody Map<String, Object> request) {
        try {
            Integer score = (Integer) request.get("score");
            String feedback = (String) request.get("feedback");
            
            ProblemSubmission submission = problemSubmissionRepository.findById(id).orElse(null);
            if (submission == null) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "제출물을 찾을 수 없습니다.");
                return ResponseEntity.notFound().build();
            }
            
            submission.setScore(score);
            submission.setFeedback(feedback);
            problemSubmissionRepository.save(submission);
            
            System.out.println("=== 점수 저장 완료 ===");
            System.out.println("제출물 ID: " + id);
            System.out.println("점수: " + score);
            System.out.println("피드백: " + feedback);
            System.out.println("================");
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "점수가 저장되었습니다.");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "점수 저장 중 오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @GetMapping("/api/teacher/submissions/{id}")
    public ResponseEntity<Map<String, Object>> getSubmission(@PathVariable Long id) {
        try {
            ProblemSubmission submission = problemSubmissionRepository.findById(id).orElse(null);
            if (submission == null) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "제출물을 찾을 수 없습니다.");
                return ResponseEntity.notFound().build();
            }
            
            Map<String, Object> result = new HashMap<>();
            result.put("id", submission.getId());
            result.put("userId", submission.getUserId());
            result.put("problemTitle", submission.getProblemTitle());
            result.put("answer", submission.getAnswer());
            result.put("submittedAt", submission.getSubmittedAt());
            result.put("score", submission.getScore());
            result.put("feedback", submission.getFeedback());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("submission", result);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "제출물 조회 중 오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @GetMapping("/api/teacher/statistics")
    public ResponseEntity<Map<String, Object>> getStatistics() {
        try {
            long totalSubmissions = problemSubmissionRepository.count();
            long gradedSubmissions = problemSubmissionRepository.countGradedSubmissions();
            long ungradedSubmissions = problemSubmissionRepository.countUngradedSubmissions();
            Double averageScore = problemSubmissionRepository.getAverageScore();
            
            long totalQuizSubmissions = quizSubmissionRepository.count();
            long correctQuizAnswers = quizSubmissionRepository.countCorrectAnswers();
            
            Map<String, Object> stats = new HashMap<>();
            stats.put("totalProblemSubmissions", totalSubmissions);
            stats.put("gradedSubmissions", gradedSubmissions);
            stats.put("ungradedSubmissions", ungradedSubmissions);
            stats.put("averageScore", averageScore != null ? averageScore : 0.0);
            stats.put("totalQuizSubmissions", totalQuizSubmissions);
            stats.put("correctQuizAnswers", correctQuizAnswers);
            stats.put("quizAccuracy", totalQuizSubmissions > 0 ? (double) correctQuizAnswers / totalQuizSubmissions * 100 : 0.0);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("statistics", stats);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "통계 조회 중 오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @GetMapping("/api/users/all")
    public ResponseEntity<Map<String, Object>> getAllUsers() {
        try {
            List<User> users = userRepository.findAll();
            
            List<Map<String, Object>> userList = users.stream().map(user -> {
                Map<String, Object> userData = new HashMap<>();
                userData.put("id", user.getId());
                userData.put("nickname", user.getNickname());
                userData.put("email", user.getEmail());
                return userData;
            }).collect(Collectors.toList());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("users", userList);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "사용자 목록 조회 실패: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @GetMapping("/api/teacher/notes")
    public ResponseEntity<Map<String, Object>> getNotes() {
        try {
            Long teacherId = 1L;
            
            List<TeacherNote> notes = teacherNoteRepository.findAllByTeacherIdOrderByCreatedAtDesc(teacherId);
            
            List<Map<String, Object>> result = notes.stream().map(note -> {
                Map<String, Object> data = new HashMap<>();
                data.put("id", note.getId());
                data.put("title", note.getTitle());
                data.put("content", note.getContent());
                data.put("category", note.getCategory());
                data.put("isPinned", note.getIsPinned());
                data.put("createdAt", note.getCreatedAt());
                data.put("updatedAt", note.getUpdatedAt());
                return data;
            }).collect(Collectors.toList());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("notes", result);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "메모 조회 실패: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/api/teacher/notes")
    public ResponseEntity<Map<String, Object>> createNote(@RequestBody Map<String, Object> request) {
        try {
            Long teacherId = 1L;
            String title = (String) request.get("title");
            String content = (String) request.get("content");
            String category = (String) request.get("category");
            
            if (title == null || title.trim().isEmpty()) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "제목은 필수입니다.");
                return ResponseEntity.badRequest().body(response);
            }
            
            TeacherNote note = new TeacherNote(teacherId, title, content, category);
            teacherNoteRepository.save(note);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "메모가 저장되었습니다.");
            response.put("noteId", note.getId());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "메모 저장 실패: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PutMapping("/api/teacher/notes/{id}")
    public ResponseEntity<Map<String, Object>> updateNote(@PathVariable Long id, @RequestBody Map<String, Object> request) {
        try {
            Long teacherId = 1L;
            
            TeacherNote note = teacherNoteRepository.findById(id).orElse(null);
            if (note == null || !note.getTeacherId().equals(teacherId)) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "메모를 찾을 수 없습니다.");
                return ResponseEntity.notFound().build();
            }
            
            String title = (String) request.get("title");
            String content = (String) request.get("content");
            String category = (String) request.get("category");
            
            if (title != null) note.setTitle(title);
            if (content != null) note.setContent(content);
            if (category != null) note.setCategory(category);
            
            teacherNoteRepository.save(note);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "메모가 수정되었습니다.");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "메모 수정 실패: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @DeleteMapping("/api/teacher/notes/{id}")
    public ResponseEntity<Map<String, Object>> deleteNote(@PathVariable Long id) {
        try {
            Long teacherId = 1L;
            
            TeacherNote note = teacherNoteRepository.findById(id).orElse(null);
            if (note == null || !note.getTeacherId().equals(teacherId)) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "메모를 찾을 수 없습니다.");
                return ResponseEntity.notFound().build();
            }
            
            teacherNoteRepository.delete(note);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "메모가 삭제되었습니다.");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "메모 삭제 실패: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}