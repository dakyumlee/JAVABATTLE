package com.javabattle.arena.web;

import com.javabattle.arena.model.User;
import com.javabattle.arena.repository.UserRepository;
import com.javabattle.arena.repository.ActiveSessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ActiveSessionRepository activeSessionRepository;

    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        List<User> allUsers = userRepository.findAll();
        
        long studentCount = allUsers.stream().filter(user -> user.getRole() == User.UserRole.STUDENT).count();
        long teacherCount = allUsers.stream().filter(user -> user.getRole() == User.UserRole.TEACHER).count();
        long adminCount = allUsers.stream().filter(user -> user.getRole() == User.UserRole.ADMIN).count();
        long activeCount = allUsers.stream().filter(User::getIsActive).count();
        long activeSessions = activeSessionRepository.count();
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsers", allUsers.size());
        stats.put("students", studentCount);
        stats.put("teachers", teacherCount);
        stats.put("admins", adminCount);
        stats.put("activeUsers", activeCount);
        stats.put("activeSessions", activeSessions);
        stats.put("systemStatus", "정상");
        
        return ResponseEntity.ok(stats);
    }

    @PostMapping("/promote-teacher")
    public ResponseEntity<String> promoteToTeacher(@RequestBody Map<String, String> request) {
        String email = request.get("email");

        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) {
            return ResponseEntity.badRequest().body("사용자를 찾을 수 없습니다.");
        }

        user.setRole(User.UserRole.TEACHER);
        userRepository.save(user);

        return ResponseEntity.ok(user.getNickname() + "님이 강사로 승급되었습니다.");
    }

    @PostMapping("/promote-admin")
    public ResponseEntity<String> promoteToAdmin(@RequestBody Map<String, String> request) {
        String email = request.get("email");

        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) {
            return ResponseEntity.badRequest().body("사용자를 찾을 수 없습니다.");
        }

        user.setRole(User.UserRole.ADMIN);
        userRepository.save(user);

        return ResponseEntity.ok(user.getNickname() + "님이 관리자로 승급되었습니다.");
    }

    @PostMapping("/create-teacher")
    public ResponseEntity<String> createTeacher(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String password = request.get("password");
        String nickname = request.get("nickname");

        if (userRepository.findByEmail(email).isPresent()) {
            return ResponseEntity.badRequest().body("이미 존재하는 이메일입니다.");
        }

        User teacher = new User(email, password, nickname, User.UserRole.TEACHER);
        userRepository.save(teacher);

        return ResponseEntity.ok("강사 계정이 생성되었습니다: " + nickname);
    }

    @PutMapping("/users/{id}/status")
    public ResponseEntity<String> updateUserStatus(@PathVariable Long id, @RequestBody Map<String, Boolean> request) {
        Boolean isActive = request.get("isActive");
        
        User user = userRepository.findById(id).orElse(null);
        if (user == null) {
            return ResponseEntity.badRequest().body("사용자를 찾을 수 없습니다.");
        }

        user.setIsActive(isActive);
        userRepository.save(user);

        return ResponseEntity.ok(user.getNickname() + "님의 계정이 " + (isActive ? "활성화" : "비활성화") + "되었습니다.");
    }
}