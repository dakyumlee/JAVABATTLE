//package com.javabattle.arena.web.auth;
//
//import com.javabattle.arena.model.User;
//import com.javabattle.arena.repository.UserRepository;
//import com.javabattle.arena.config.JwtUtil;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.HashMap;
//import java.util.Map;
//
//@RestController
//@RequestMapping("/api/auth")
//@CrossOrigin(origins = "*")
//public class AuthController {
//   
//   @Autowired
//   private UserRepository userRepository;
//   
//   @Autowired
//   private JwtUtil jwtUtil;
//   
//   @PostMapping("/login")
//   public ResponseEntity<Map<String, Object>> login(@RequestBody LoginRequest request) {
//       try {
//           User user = userRepository.findByEmail(request.getEmail()).orElse(null);
//           
//           Map<String, Object> response = new HashMap<>();
//           
//           if (user != null && user.getPassword().equals(request.getPassword())) {
//               String token = jwtUtil.generateToken(user.getEmail(), user.getId());
//               
//               response.put("status", "success");
//               response.put("token", token);
//               response.put("nickname", user.getNickname());
//               response.put("id", user.getId());
//               response.put("email", user.getEmail());
//               response.put("role", user.getRole().name());
//               response.put("level", user.getLevel());
//               
//               return ResponseEntity.ok(response);
//           } else {
//               response.put("status", "error");
//               response.put("message", "이메일 또는 비밀번호가 잘못되었습니다");
//               return ResponseEntity.badRequest().body(response);
//           }
//       } catch (Exception e) {
//           Map<String, Object> response = new HashMap<>();
//           response.put("status", "error");
//           response.put("message", "로그인 처리 중 오류가 발생했습니다: " + e.getMessage());
//           return ResponseEntity.status(500).body(response);
//       }
//   }
//   
//   @PostMapping("/register")
//   public ResponseEntity<Map<String, Object>> register(@RequestBody RegisterRequest request) {
//       try {
//           Map<String, Object> response = new HashMap<>();
//           
//           if (userRepository.existsByEmail(request.getEmail())) {
//               response.put("status", "error");
//               response.put("message", "이미 사용중인 이메일입니다");
//               return ResponseEntity.badRequest().body(response);
//           }
//           
//           User user = new User();
//           user.setEmail(request.getEmail());
//           user.setPassword(request.getPassword());
//           user.setNickname(request.getNickname());
//           user.setRole(User.UserRole.STUDENT);
//           
//           userRepository.save(user);
//           
//           response.put("status", "success");
//           response.put("message", "회원가입이 완료되었습니다. 로그인해주세요.");
//           return ResponseEntity.ok(response);
//           
//       } catch (Exception e) {
//           Map<String, Object> response = new HashMap<>();
//           response.put("status", "error");
//           response.put("message", "회원가입 중 오류가 발생했습니다: " + e.getMessage());
//           return ResponseEntity.status(500).body(response);
//       }
//   }
//   
//   @GetMapping("/profile")
//   public ResponseEntity<Map<String, Object>> getProfile(@RequestHeader("Authorization") String token) {
//       try {
//           String email = jwtUtil.extractUsername(token.substring(7));
//           User user = userRepository.findByEmail(email).orElse(null);
//           
//           Map<String, Object> response = new HashMap<>();
//           
//           if (user != null) {
//               response.put("id", user.getId());
//               response.put("email", user.getEmail());
//               response.put("nickname", user.getNickname());
//               response.put("role", user.getRole().name());
//               response.put("level", user.getLevel());
//               return ResponseEntity.ok(response);
//           }
//           
//           response.put("status", "error");
//           response.put("message", "사용자를 찾을 수 없습니다");
//           return ResponseEntity.badRequest().body(response);
//           
//       } catch (Exception e) {
//           Map<String, Object> response = new HashMap<>();
//           response.put("status", "error");
//           response.put("message", "유효하지 않은 토큰입니다: " + e.getMessage());
//           return ResponseEntity.badRequest().body(response);
//       }
//   }
//   
//   public static class LoginRequest {
//       private String email;
//       private String password;
//       
//       public String getEmail() { return email; }
//       public void setEmail(String email) { this.email = email; }
//       public String getPassword() { return password; }
//       public void setPassword(String password) { this.password = password; }
//   }
//   
//   public static class RegisterRequest {
//       private String email;
//       private String password;
//       private String nickname;
//       
//       public String getEmail() { return email; }
//       public void setEmail(String email) { this.email = email; }
//       public String getPassword() { return password; }
//       public void setPassword(String password) { this.password = password; }
//       public String getNickname() { return nickname; }
//       public void setNickname(String nickname) { this.nickname = nickname; }
//   }
//}


package com.javabattle.arena.web.auth;

import com.javabattle.arena.model.User;
import com.javabattle.arena.repository.UserRepository;
import com.javabattle.arena.config.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginRequest request) {
        System.out.println("로그인 요청 받음: " + request.getEmail());
        
        try {
            Map<String, Object> response = new HashMap<>();
            
            System.out.println("DB에서 사용자 검색 중: " + request.getEmail());
            User user = userRepository.findByEmail(request.getEmail()).orElse(null);
            
            if (user == null) {
                System.out.println("사용자를 찾을 수 없음: " + request.getEmail());
                response.put("status", "error");
                response.put("message", "사용자를 찾을 수 없습니다");
                return ResponseEntity.badRequest().body(response);
            }
            
            System.out.println("사용자 찾음: " + user.getNickname());
            System.out.println("비밀번호 검증 중...");
            
            if (!user.getPassword().equals(request.getPassword())) {
                System.out.println("비밀번호 불일치");
                response.put("status", "error");
                response.put("message", "비밀번호가 잘못되었습니다");
                return ResponseEntity.badRequest().body(response);
            }
            
            System.out.println("JWT 토큰 생성 중...");
            String token = jwtUtil.generateToken(user.getEmail(), user.getId());
            System.out.println("토큰 생성 완료: " + token.substring(0, 20) + "...");
            
            response.put("status", "success");
            response.put("token", token);
            response.put("nickname", user.getNickname());
            response.put("id", user.getId());
            response.put("email", user.getEmail());
            response.put("role", user.getRole().name());
            response.put("level", user.getLevel());
            
            System.out.println("로그인 성공 응답 전송");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            System.out.println("로그인 처리 중 예외 발생: " + e.getClass().getSimpleName());
            System.out.println("예외 메시지: " + e.getMessage());
            e.printStackTrace();
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "로그인 처리 중 오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody RegisterRequest request) {
        try {
            Map<String, Object> response = new HashMap<>();
            
            if (userRepository.existsByEmail(request.getEmail())) {
                response.put("status", "error");
                response.put("message", "이미 사용중인 이메일입니다");
                return ResponseEntity.badRequest().body(response);
            }
            
            User user = new User();
            user.setEmail(request.getEmail());
            user.setPassword(request.getPassword());
            user.setNickname(request.getNickname());
            user.setRole(User.UserRole.STUDENT);
            
            userRepository.save(user);
            
            response.put("status", "success");
            response.put("message", "회원가입이 완료되었습니다. 로그인해주세요.");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "회원가입 중 오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    
    @GetMapping("/profile")
    public ResponseEntity<Map<String, Object>> getProfile(@RequestHeader("Authorization") String token) {
        try {
            String email = jwtUtil.extractUsername(token.substring(7));
            User user = userRepository.findByEmail(email).orElse(null);
            
            Map<String, Object> response = new HashMap<>();
            
            if (user != null) {
                response.put("id", user.getId());
                response.put("email", user.getEmail());
                response.put("nickname", user.getNickname());
                response.put("role", user.getRole().name());
                response.put("level", user.getLevel());
                return ResponseEntity.ok(response);
            }
            
            response.put("status", "error");
            response.put("message", "사용자를 찾을 수 없습니다");
            return ResponseEntity.badRequest().body(response);
            
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "유효하지 않은 토큰입니다: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    public static class LoginRequest {
        private String email;
        private String password;
        
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }
    
    public static class RegisterRequest {
        private String email;
        private String password;
        private String nickname;
        
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        public String getNickname() { return nickname; }
        public void setNickname(String nickname) { this.nickname = nickname; }
    }
}