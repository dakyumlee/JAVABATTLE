package com.javabattle.arena.web;

import com.javabattle.arena.model.User;
import com.javabattle.arena.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminController {
    
    @Autowired
    private UserRepository userRepository;
    
    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userRepository.findAll();
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
}
