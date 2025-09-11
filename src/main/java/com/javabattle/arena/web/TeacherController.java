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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.Base64;
import java.net.URLEncoder;
import java.io.UnsupportedEncodingException;

@Controller
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
    @ResponseBody
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
    @ResponseBody
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
    @ResponseBody
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
    @ResponseBody
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
    @ResponseBody
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
    @ResponseBody
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
    @ResponseBody
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
    @ResponseBody
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
    @ResponseBody
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
    @ResponseBody
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
    @ResponseBody
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
    @ResponseBody
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
    @ResponseBody
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
    @ResponseBody
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

    @PostMapping("/api/teacher/materials/upload")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> uploadMaterial(
            @RequestParam(value = "file", required = false) MultipartFile file,
            @RequestParam("title") String title,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "tags", required = false) String tags) {
        
        try {
            System.out.println("=== 자료 업로드 시작 ===");
            System.out.println("Title: " + title);
            System.out.println("Description: " + description);
            System.out.println("Category: " + category);
            System.out.println("File: " + (file != null ? file.getOriginalFilename() : "없음"));
            
            TeacherMaterial material = new TeacherMaterial();
            material.setTitle(title);
            material.setDescription(description);
            material.setCategory(category);
            material.setTags(tags);
            material.setTeacherId(1L);
            material.setIsShared(false);
            material.setCreatedAt(LocalDateTime.now());
            
            if (file != null && !file.isEmpty()) {
                System.out.println("파일 처리 중: " + file.getOriginalFilename());
                System.out.println("파일 크기: " + file.getSize());
                
                material.setFileName(file.getOriginalFilename());
                material.setFileSize(file.getSize());
                material.setMaterialType(file.getContentType());
                
                try {
                    byte[] fileBytes = file.getBytes();
                    System.out.println("파일 바이트 읽기 성공: " + fileBytes.length);
                    material.setFileData(fileBytes);
                } catch (Exception e) {
                    System.out.println("파일 바이트 읽기 실패: " + e.getMessage());
                    throw e;
                }
            } else {
                System.out.println("텍스트 자료로 처리");
                material.setMaterialType("text");
                material.setContent(description);
            }

            System.out.println("DB 저장 시작");
            TeacherMaterial saved = teacherMaterialRepository.save(material);
            System.out.println("DB 저장 완료. ID: " + saved.getId());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "자료가 성공적으로 업로드되었습니다.");
            response.put("materialId", saved.getId());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            System.out.println("업로드 실패: " + e.getMessage());
            e.printStackTrace();
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "자료 업로드 중 오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/api/teacher/materials")
    @ResponseBody
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
    @ResponseBody
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
    @ResponseBody
    public ResponseEntity<Map<String, Object>> shareMaterial(@PathVariable Long id, HttpServletRequest request) {
        try {
            System.out.println("=== 자료 공유 요청 ===");
            System.out.println("Material ID: " + id);
            System.out.println("요청 헤더: " + request.getHeader("Content-Type"));
            System.out.println("요청 메서드: " + request.getMethod());
            
            TeacherMaterial material = teacherMaterialRepository.findById(id).orElse(null);
            if (material == null) {
                System.out.println("자료를 찾을 수 없음: " + id);
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "자료를 찾을 수 없습니다.");
                return ResponseEntity.notFound().build();
            }
            
            System.out.println("찾은 자료: " + material.getTitle());
            System.out.println("현재 공유 상태: " + material.getIsShared());
            
            boolean previouslyShared = material.getIsShared() != null && material.getIsShared();
            material.setIsShared(!previouslyShared);
            
            System.out.println("공유 상태 변경: " + previouslyShared + " -> " + material.getIsShared());
            
            TeacherMaterial saved = teacherMaterialRepository.save(material);
            System.out.println("DB 저장 완료. 새 공유 상태: " + saved.getIsShared());

            if (saved.getIsShared()) {
                Map<String, Object> materialData = new HashMap<>();
                materialData.put("type", "NEW_MATERIAL");
                materialData.put("title", saved.getTitle());
                materialData.put("content", saved.getDescription());
                materialData.put("materialType", saved.getMaterialType());
                materialData.put("from", "teacher");
                materialData.put("timestamp", LocalDateTime.now());
                materialData.put("materialId", saved.getId());
                
                System.out.println("WebSocket 알림 전송 중...");
                System.out.println("알림 데이터: " + materialData);
                
                try {
                    messagingTemplate.convertAndSend("/topic/teacher-announcements", materialData);
                    System.out.println("WebSocket 알림 전송 완료");
                } catch (Exception wsError) {
                    System.out.println("WebSocket 알림 전송 실패: " + wsError.getMessage());
                    wsError.printStackTrace();
                }
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("shared", saved.getIsShared());
            response.put("message", saved.getIsShared() ? "학생들에게 공유되었습니다." : "공유가 중지되었습니다.");
            
            System.out.println("최종 응답: " + response);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            System.out.println("공유 처리 중 예외 발생: " + e.getClass().getSimpleName());
            System.out.println("예외 메시지: " + e.getMessage());
            e.printStackTrace();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "공유 설정 중 오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PutMapping("/api/teacher/materials/{id}")
    @ResponseBody
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

    @GetMapping("/api/teacher/materials/shared")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getSharedMaterials() {
        try {
            List<TeacherMaterial> materials = teacherMaterialRepository.findSharedMaterials();
            
            List<Map<String, Object>> materialList = materials.stream().map(material -> {
                Map<String, Object> data = new HashMap<>();
                data.put("id", material.getId());
                data.put("title", material.getTitle());
                data.put("content", material.getContent() != null ? material.getContent() : material.getDescription());
                data.put("description", material.getDescription());
                data.put("materialType", material.getMaterialType());
                data.put("fileName", material.getFileName());
                data.put("fileSize", material.getFileSize());
                data.put("youtubeUrl", material.getYoutubeUrl());
                data.put("createdAt", material.getCreatedAt());
                data.put("category", material.getCategory());
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

    @GetMapping("/api/teacher/materials/{id}/download")
    @ResponseBody
    public ResponseEntity<Resource> downloadMaterial(@PathVariable Long id) {
        try {
            System.out.println("=== 파일 다운로드 요청 ===");
            System.out.println("Material ID: " + id);
            
            Optional<TeacherMaterial> materialOpt = teacherMaterialRepository.findById(id);
            if (!materialOpt.isPresent()) {
                System.out.println("자료를 찾을 수 없음");
                return ResponseEntity.notFound().build();
            }
            
            TeacherMaterial material = materialOpt.get();
            System.out.println("자료 정보: " + material.getTitle());
            System.out.println("파일명: " + material.getFileName());
            System.out.println("파일 데이터 존재: " + (material.getFileData() != null));
            
            if (material.getFileData() == null || material.getFileData().length == 0) {
                System.out.println("파일 데이터가 없음");
                return ResponseEntity.notFound().build();
            }
            
            System.out.println("파일 크기: " + material.getFileData().length);
            
            ByteArrayResource resource = new ByteArrayResource(material.getFileData());
            
            String filename = material.getFileName();
            if (filename == null || filename.isEmpty()) {
                filename = "material_" + id;
            }
            
            String encodedFilename;
            try {
                encodedFilename = URLEncoder.encode(filename, "UTF-8").replaceAll("\\+", "%20");
            } catch (UnsupportedEncodingException e) {
                encodedFilename = filename;
            }
            
            System.out.println("다운로드 파일명: " + filename);
            
            return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, 
                    "attachment; filename=\"" + encodedFilename + "\"")
                .contentType(MediaType.parseMediaType(material.getMaterialType() != null ? 
                    material.getMaterialType() : "application/octet-stream"))
                .contentLength(material.getFileData().length)
                .body(resource);
                
        } catch (Exception e) {
            System.out.println("다운로드 실패: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/material-preview/{id}")
    public String viewMaterial(@PathVariable Long id, Model model) {
        try {
            System.out.println("=== 자료 미리보기 요청 ===");
            System.out.println("Material ID: " + id);
            
            Optional<TeacherMaterial> materialOpt = teacherMaterialRepository.findById(id);
            if (!materialOpt.isPresent()) {
                System.out.println("자료를 찾을 수 없음");
                model.addAttribute("error", "자료를 찾을 수 없습니다.");
                return "material-error";
            }
            
            TeacherMaterial material = materialOpt.get();
            String fileType = material.getMaterialType();
            
            System.out.println("자료 정보:");
            System.out.println("- 제목: " + material.getTitle());
            System.out.println("- 파일명: " + material.getFileName());
            System.out.println("- 타입: " + fileType);
            System.out.println("- 파일 데이터: " + (material.getFileData() != null ? material.getFileData().length + " bytes" : "없음"));
            
            if (fileType == null) {
                fileType = "text/plain";
            }
            
            model.addAttribute("material", material);
            model.addAttribute("materialId", id);
            
            if (material.getFileData() != null && material.getFileData().length > 0) {
                if (fileType.startsWith("image/")) {
                    String base64Data = Base64.getEncoder().encodeToString(material.getFileData());
                    String dataUrl = "data:" + fileType + ";base64," + base64Data;
                    model.addAttribute("filePath", dataUrl);
                    System.out.println("이미지 미리보기 준비 완료");
                    return "material-preview-image";
                } else if (fileType.equals("application/pdf")) {
                    model.addAttribute("filePath", "/api/teacher/materials/" + id + "/download");
                    System.out.println("PDF 미리보기 준비 완료");
                    return "material-preview-pdf";
                } else if (fileType.startsWith("text/") || fileType.equals("application/json") || fileType.equals("text")) {
                    String textContent = new String(material.getFileData());
                    model.addAttribute("textContent", textContent);
                    System.out.println("텍스트 미리보기 준비 완료");
                    return "material-preview-text";
                }
            } else {
                System.out.println("파일 데이터 없음, description 사용");
                if (material.getDescription() != null && !material.getDescription().trim().isEmpty()) {
                    model.addAttribute("textContent", material.getDescription());
                    return "material-preview-text";
                }
            }
            
            String extension = "";
            if (material.getFileName() != null && material.getFileName().contains(".")) {
                extension = material.getFileName().substring(material.getFileName().lastIndexOf(".") + 1);
            }
            model.addAttribute("fileExtension", extension.toUpperCase());
            System.out.println("일반 파일 템플릿 반환: " + extension);
            return "material-preview-file";
            
        } catch (Exception e) {
            System.out.println("미리보기 실패: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("error", "미리보기 중 오류가 발생했습니다: " + e.getMessage());
            return "material-error";
        }
    }
    
    @GetMapping("/api/teacher/materials/{id}/view")
    @ResponseBody
    public ResponseEntity<String> viewMaterialRedirect(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.FOUND)
            .header(HttpHeaders.LOCATION, "/material-preview/" + id)
            .build();
    }
}