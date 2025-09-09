package com.javabattle.arena.web;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import java.util.Map;

@RestController
@RequestMapping("/api/session")
public class SessionController {
    
    @PostMapping("/update")
    public ResponseEntity<?> updateActivity(@RequestBody Map<String, Object> requestData) {
        // CLOB 문제로 인해 세션 업데이트 기능 비활성화
        System.out.println("세션 업데이트 요청 무시됨 (CLOB 문제)");
        return ResponseEntity.ok(Map.of("status", "ignored"));
    }
    
    @PostMapping("/start")
    public ResponseEntity<?> startSession(@RequestBody Map<String, Object> requestData) {
        System.out.println("세션 시작 요청 무시됨");
        return ResponseEntity.ok(Map.of("status", "ignored"));
    }
    
    @PostMapping("/end")
    public ResponseEntity<?> endSession(@RequestBody Map<String, Object> requestData) {
        System.out.println("세션 종료 요청 무시됨");
        return ResponseEntity.ok(Map.of("status", "ignored"));
    }
}
