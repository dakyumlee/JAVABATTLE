package com.javabattle.arena.web;

import com.javabattle.arena.model.ActiveSession;
import com.javabattle.arena.model.ProblemSubmission;
import com.javabattle.arena.model.QuizSubmission;
import com.javabattle.arena.model.User;
import com.javabattle.arena.model.TeacherNote;
import com.javabattle.arena.model.TeacherMaterial;
import com.javabattle.arena.repository.ProblemSubmissionRepository;
import com.javabattle.arena.repository.QuizSubmissionRepository;
import com.javabattle.arena.repository.UserRepository;
import com.javabattle.arena.repository.TeacherNoteRepository;
import com.javabattle.arena.repository.TeacherMaterialRepository;
import com.javabattle.arena.service.SessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
    
    @Autowired
    private TeacherMaterialRepository teacherMaterialRepository;
    
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
    
    @PostMapping("/api/teacher/notes")
    public ResponseEntity<Map<String, Object>> createNote(@RequestBody Map<String, Object> noteData) {
        try {
            TeacherNote note = new TeacherNote();
            note.setTitle((String) noteData.get("title"));
            note.setContent((String) noteData.get("content"));
            note.setCategory((String) noteData.get("category"));
            note.setTeacherId(1L);
            note.setIsPinned(false);
            note.setCreatedAt(LocalDateTime.now());
            note.setUpdatedAt(LocalDateTime.now());
            
            TeacherNote saved = teacherNoteRepository.save(note);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("note", saved);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "노트 생성 실패: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @GetMapping("/api/teacher/notes")
    public ResponseEntity<Map<String, Object>> getNotes() {
        try {
            List<TeacherNote> notes = teacherNoteRepository.findAll();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("notes", notes);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "노트 조회 실패: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @DeleteMapping("/api/teacher/notes/{id}")
    public ResponseEntity<Map<String, Object>> deleteNote(@PathVariable Long id) {
        try {
            teacherNoteRepository.deleteById(id);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "노트가 삭제되었습니다.");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "노트 삭제 실패: " + e.getMessage());
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

    @PostMapping("/api/teacher/shared-materials")
    public ResponseEntity<Map<String, Object>> shareTeacherMaterial(
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestParam(value = "file", required = false) MultipartFile file) {
        
        try {
            TeacherMaterial material = new TeacherMaterial();
            material.setTitle(title);
            material.setContent(content);
            material.setCreatedAt(LocalDateTime.now());
            material.setTeacherId(1L);
            material.setIsShared(true);
            
            if (file != null && !file.isEmpty()) {
                material.setFileName(file.getOriginalFilename());
                material.setFileSize(file.getSize());
                material.setMaterialType(file.getContentType());
                material.setFileData(file.getBytes());
            } else {
                material.setMaterialType("text");
            }

            TeacherMaterial saved = teacherMaterialRepository.save(material);

            Map<String, Object> materialData = new HashMap<>();
            materialData.put("type", "NEW_MATERIAL");
            materialData.put("title", saved.getTitle());
            materialData.put("content", saved.getContent());
            materialData.put("materialType", saved.getMaterialType());
            materialData.put("from", "teacher");
            materialData.put("timestamp", LocalDateTime.now());
            materialData.put("materialId", saved.getId());

            messagingTemplate.convertAndSend("/topic/teacher-announcements", materialData);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "자료가 성공적으로 공유되었습니다.");
            response.put("materialId", saved.getId());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "자료 공유 중 오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/api/teacher/materials/shared")
    public ResponseEntity<Map<String, Object>> getSharedMaterials() {
        try {
            List<TeacherMaterial> materials = teacherMaterialRepository.findSharedMaterials();
            
            List<Map<String, Object>> materialList = materials.stream().map(material -> {
                Map<String, Object> data = new HashMap<>();
                data.put("id", material.getId());
                data.put("title", material.getTitle());
                data.put("content", material.getContent());
                data.put("materialType", material.getMaterialType());
                data.put("fileName", material.getFileName());
                data.put("fileSize", material.getFileSize());
                data.put("youtubeUrl", material.getYoutubeUrl());
                data.put("createdAt", material.getCreatedAt());
                return data;
            }).collect(Collectors.toList());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("materials", materialList);
            response.put("totalCount", materialList.size());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "공유 자료 조회 중 오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/api/teacher/materials/upload")
    public ResponseEntity<Map<String, Object>> uploadMaterial(
            @RequestParam(value = "file", required = false) MultipartFile file,
            @RequestParam("title") String title,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "tags", required = false) String tags) {
        
        try {
            TeacherMaterial material = new TeacherMaterial();
            material.setTitle(title);
            material.setDescription(description);
            material.setCategory(category);
            material.setTags(tags);
            material.setTeacherId(1L);
            material.setIsShared(false);
            material.setCreatedAt(LocalDateTime.now());
            
            if (file != null && !file.isEmpty()) {
                material.setFileName(file.getOriginalFilename());
                material.setFileSize(file.getSize());
                material.setMaterialType(file.getContentType());
                material.setFileData(file.getBytes());
            } else {
                material.setMaterialType("text");
            }

            TeacherMaterial saved = teacherMaterialRepository.save(material);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "자료가 성공적으로 업로드되었습니다.");
            response.put("materialId", saved.getId());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "자료 업로드 중 오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/api/teacher/materials")
    public ResponseEntity<List<Map<String, Object>>> getMaterials() {
        try {
            List<TeacherMaterial> materials = teacherMaterialRepository.findAllOrderByCreatedAtDesc();
            
            List<Map<String, Object>> materialList = materials.stream().map(material -> {
                Map<String, Object> data = new HashMap<>();
                data.put("id", material.getId());
                data.put("title", material.getTitle());
                data.put("description", material.getDescription());
                data.put("category", material.getCategory());
                data.put("tags", material.getTags());
                data.put("materialType", material.getMaterialType());
                data.put("fileName", material.getFileName());
                data.put("fileSize", material.getFileSize());
                data.put("youtubeUrl", material.getYoutubeUrl());
                data.put("createdAt", material.getCreatedAt());
                data.put("shared", material.getIsShared());
                return data;
            }).collect(Collectors.toList());
            
            return ResponseEntity.ok(materialList);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/api/teacher/materials/{id}")
    public ResponseEntity<Map<String, Object>> deleteMaterial(@PathVariable Long id) {
        try {
            if (!teacherMaterialRepository.existsById(id)) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "자료를 찾을 수 없습니다.");
                return ResponseEntity.notFound().build();
            }
            
            teacherMaterialRepository.deleteById(id);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "자료가 삭제되었습니다.");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "삭제 중 오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/api/teacher/materials/{id}/share")
    public ResponseEntity<Map<String, Object>> shareMaterial(@PathVariable Long id) {
        try {
            TeacherMaterial material = teacherMaterialRepository.findById(id).orElse(null);
            if (material == null) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "자료를 찾을 수 없습니다.");
                return ResponseEntity.notFound().build();
            }
            
            material.setIsShared(!material.getIsShared());
            teacherMaterialRepository.save(material);

            if (material.getIsShared()) {
                Map<String, Object> materialData = new HashMap<>();
                materialData.put("type", "NEW_MATERIAL");
                materialData.put("title", material.getTitle());
                materialData.put("content", material.getDescription());
                materialData.put("materialType", material.getMaterialType());
                materialData.put("from", "teacher");
                materialData.put("timestamp", LocalDateTime.now());
                materialData.put("materialId", material.getId());
                messagingTemplate.convertAndSend("/topic/teacher-announcements", materialData);
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("shared", material.getIsShared());
            response.put("message", material.getIsShared() ? "학생들에게 공유되었습니다." : "공유가 중지되었습니다.");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "공유 설정 중 오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PutMapping("/api/teacher/materials/{id}")
    public ResponseEntity<Map<String, Object>> updateMaterial(@PathVariable Long id, @RequestBody Map<String, String> request) {
        try {
            TeacherMaterial material = teacherMaterialRepository.findById(id).orElse(null);
            if (material == null) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "자료를 찾을 수 없습니다.");
                return ResponseEntity.notFound().build();
            }
            
            if (request.get("title") != null) {
                material.setTitle(request.get("title"));
            }
            if (request.get("description") != null) {
                material.setDescription(request.get("description"));
            }
            if (request.get("category") != null) {
                material.setCategory(request.get("category"));
            }
            if (request.get("tags") != null) {
                material.setTags(request.get("tags"));
            }
            
            teacherMaterialRepository.save(material);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "자료가 수정되었습니다.");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "수정 중 오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/api/teacher/materials/{id}/download")
    public ResponseEntity<Resource> downloadMaterial(@PathVariable Long id) {
        try {
            Optional<TeacherMaterial> materialOpt = teacherMaterialRepository.findById(id);
            if (!materialOpt.isPresent()) {
                return ResponseEntity.notFound().build();
            }
            
            TeacherMaterial material = materialOpt.get();
            
            if (material.getFileData() == null) {
                return ResponseEntity.notFound().build();
            }
            
            ByteArrayResource resource = new ByteArrayResource(material.getFileData());
            
            return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, 
                    "attachment; filename=\"" + material.getFileName() + "\"")
                .header(HttpHeaders.CONTENT_TYPE, material.getMaterialType())
                .body(resource);
                
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/api/teacher/materials/{id}/view")
    public ResponseEntity<Resource> viewMaterial(@PathVariable Long id) {
        try {
            Optional<TeacherMaterial> materialOpt = teacherMaterialRepository.findById(id);
            if (!materialOpt.isPresent()) {
                return ResponseEntity.notFound().build();
            }
            
            TeacherMaterial material = materialOpt.get();
            
            if (material.getFileData() == null) {
                return ResponseEntity.notFound().build();
            }
            
            ByteArrayResource resource = new ByteArrayResource(material.getFileData());
            
            return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline")
                .header(HttpHeaders.CONTENT_TYPE, material.getMaterialType())
                .body(resource);
                
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}