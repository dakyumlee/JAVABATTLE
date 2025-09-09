package com.javabattle.arena.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/test")
public class TestController {
    
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    
    @PostMapping("/websocket")
    public Map<String, String> testWebSocket(@RequestBody Map<String, String> request) {
        System.out.println("REST API로 WebSocket 테스트 요청: " + request);
        
        try {
            messagingTemplate.convertAndSend("/topic/test", "REST에서 전송한 메시지: " + request.get("message"));
            return Map.of("status", "success", "message", "WebSocket 메시지 전송됨");
        } catch (Exception e) {
            System.err.println("WebSocket 메시지 전송 실패: " + e.getMessage());
            return Map.of("status", "error", "message", e.getMessage());
        }
    }
}
