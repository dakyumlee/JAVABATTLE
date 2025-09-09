package com.javabattle.arena.web;

import com.javabattle.arena.model.TeacherMaterial;
import com.javabattle.arena.model.Quiz;
import com.javabattle.arena.repository.TeacherMaterialRepository;
import com.javabattle.arena.repository.QuizRepository;
import com.javabattle.arena.config.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Controller
@RequestMapping("/api/teacher")
public class TeacherMaterialController {

   @Autowired
   private TeacherMaterialRepository materialRepository;

   @Autowired
   private QuizRepository quizRepository;

   @Autowired
   private JwtUtil jwtUtil;

   @GetMapping("/teacher-materials")
   public String teacherMaterials() {
       return "teacher-materials";
   }

   @PostMapping("/materials/upload")
   @ResponseBody
   public ResponseEntity<?> uploadMaterial(@RequestBody Map<String, String> request,
                                         HttpServletRequest httpRequest) {
       try {
           String token = extractToken(httpRequest);
           Long userId = jwtUtil.extractUserId(token);

           TeacherMaterial material = new TeacherMaterial();
           material.setTitle(request.get("title"));
           material.setDescription(request.get("description"));
           material.setCategory(request.get("category"));
           material.setTags(request.get("tags"));
           material.setFileType("text");
           material.setUploadedBy(userId);
           material.setCreatedAt(LocalDateTime.now());
           material.setShared(false);

           materialRepository.save(material);

           Map<String, Object> response = new HashMap<>();
           response.put("success", true);
           response.put("message", "자료가 성공적으로 업로드되었습니다.");
           response.put("materialId", material.getId());

           return ResponseEntity.ok(response);
       } catch (Exception e) {
           Map<String, Object> response = new HashMap<>();
           response.put("success", false);
           response.put("message", "업로드 중 오류가 발생했습니다: " + e.getMessage());
           return ResponseEntity.badRequest().body(response);
       }
   }

   @PostMapping("/materials/youtube")
   @ResponseBody
   public ResponseEntity<?> addYouTubeLink(@RequestBody Map<String, String> request,
                                         HttpServletRequest httpRequest) {
       try {
           String token = extractToken(httpRequest);
           Long userId = jwtUtil.extractUserId(token);

           TeacherMaterial material = new TeacherMaterial();
           material.setTitle(request.get("title"));
           material.setDescription(request.get("description"));
           material.setCategory(request.get("category"));
           material.setTags(request.get("tags"));
           material.setYoutubeUrl(request.get("url"));
           material.setFileType("youtube");
           material.setUploadedBy(userId);
           material.setCreatedAt(LocalDateTime.now());
           material.setShared(false);

           materialRepository.save(material);

           Map<String, Object> response = new HashMap<>();
           response.put("success", true);
           response.put("message", "YouTube 링크가 추가되었습니다.");

           return ResponseEntity.ok(response);
       } catch (Exception e) {
           Map<String, Object> response = new HashMap<>();
           response.put("success", false);
           response.put("message", "YouTube 링크 추가 중 오류가 발생했습니다.");
           return ResponseEntity.badRequest().body(response);
       }
   }

   @GetMapping("/materials")
   @ResponseBody
   public ResponseEntity<List<TeacherMaterial>> getMaterials(HttpServletRequest request) {
       try {
           String token = extractToken(request);
           Long userId = jwtUtil.extractUserId(token);
           
           List<TeacherMaterial> materials = materialRepository.findByUploadedByOrderByCreatedAtDesc(userId);
           return ResponseEntity.ok(materials);
       } catch (Exception e) {
           return ResponseEntity.badRequest().build();
       }
   }

   @GetMapping("/shared-materials")
   @ResponseBody
   public ResponseEntity<List<TeacherMaterial>> getSharedMaterials() {
       try {
           List<TeacherMaterial> sharedMaterials = materialRepository.findBySharedTrueOrderByCreatedAtDesc();
           return ResponseEntity.ok(sharedMaterials);
       } catch (Exception e) {
           return ResponseEntity.badRequest().build();
       }
   }

   @PostMapping("/materials/{id}/share")
   @ResponseBody
   public ResponseEntity<?> shareMaterial(@PathVariable Long id, HttpServletRequest request) {
       try {
           String token = extractToken(request);
           Long userId = jwtUtil.extractUserId(token);

           TeacherMaterial material = materialRepository.findById(id).orElse(null);
           if (material == null || !material.getUploadedBy().equals(userId)) {
               return ResponseEntity.badRequest().body(Map.of("success", false, "message", "자료를 찾을 수 없습니다."));
           }

           material.setShared(!material.getShared());
           materialRepository.save(material);

           Map<String, Object> response = new HashMap<>();
           response.put("success", true);
           response.put("shared", material.getShared());
           response.put("message", material.getShared() ? "학생들에게 공유되었습니다." : "공유가 중지되었습니다.");

           return ResponseEntity.ok(response);
       } catch (Exception e) {
           return ResponseEntity.badRequest().body(Map.of("success", false, "message", "공유 설정 중 오류가 발생했습니다."));
       }
   }

   @DeleteMapping("/materials/{id}")
   @ResponseBody
   public ResponseEntity<?> deleteMaterial(@PathVariable Long id, HttpServletRequest request) {
       try {
           String token = extractToken(request);
           Long userId = jwtUtil.extractUserId(token);

           TeacherMaterial material = materialRepository.findById(id).orElse(null);
           if (material == null || !material.getUploadedBy().equals(userId)) {
               return ResponseEntity.badRequest().body(Map.of("success", false, "message", "자료를 찾을 수 없습니다."));
           }

           materialRepository.delete(material);

           return ResponseEntity.ok(Map.of("success", true, "message", "자료가 삭제되었습니다."));
       } catch (Exception e) {
           return ResponseEntity.badRequest().body(Map.of("success", false, "message", "삭제 중 오류가 발생했습니다."));
       }
   }

   @PostMapping("/quiz")
   @ResponseBody
   public ResponseEntity<?> createQuiz(@RequestBody Map<String, Object> request,
                                     HttpServletRequest httpRequest) {
       try {
           String token = extractToken(httpRequest);
           Long userId = jwtUtil.extractUserId(token);

           Quiz quiz = new Quiz();
           quiz.setTitle((String) request.get("title"));
           quiz.setQuestion((String) request.get("question"));
           quiz.setOptions(String.join(",", (List<String>) request.get("options")));
           quiz.setCorrectAnswer((Integer) request.get("correctAnswer"));
           quiz.setScheduleType((String) request.get("schedule"));
           quiz.setCreatedBy(userId);

           if ("later".equals(request.get("schedule")) && request.get("scheduleTime") != null) {
               quiz.setScheduleTime(LocalDateTime.parse((String) request.get("scheduleTime")));
           }

           quizRepository.save(quiz);

           Map<String, Object> response = new HashMap<>();
           response.put("success", true);
           response.put("message", "퀴즈가 생성되었습니다.");

           return ResponseEntity.ok(response);
       } catch (Exception e) {
           Map<String, Object> response = new HashMap<>();
           response.put("success", false);
           response.put("message", "퀴즈 생성 중 오류가 발생했습니다: " + e.getMessage());
           return ResponseEntity.badRequest().body(response);
       }
   }

   @GetMapping("/quiz/list")
   @ResponseBody
   public ResponseEntity<List<Quiz>> getQuizList(HttpServletRequest request) {
       try {
           String token = extractToken(request);
           Long userId = jwtUtil.extractUserId(token);
           
           List<Quiz> quizzes = quizRepository.findByCreatedByOrderByCreatedAtDesc(userId);
           return ResponseEntity.ok(quizzes);
       } catch (Exception e) {
           return ResponseEntity.badRequest().build();
       }
   }

   private String extractToken(HttpServletRequest request) {
       String bearerToken = request.getHeader("Authorization");
       if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
           return bearerToken.substring(7);
       }
       return null;
   }
}
