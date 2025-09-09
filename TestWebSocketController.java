package com.javabattle.arena.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import java.util.Map;

@Controller
public class TestWebSocketController {
    
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    
    @MessageMapping("/test")
    public void handleTestMessage(@Payload String message) {
        System.out.println("=== 테스트 메시지 수신 ===");
        System.out.println("메시지: " + message);
        
        messagingTemplate.convertAndSend("/topic/test", "서버 응답: " + message);
        System.out.println("응답 전송 완료");
    }
    
    @MessageMapping("/battle/create-room")
    public void createRoom(@Payload Map<String, Object> message) {
        System.out.println("=== 배틀 방 생성 요청 ===");
        System.out.println("메시지: " + message);
        
        try {
            String roomName = (String) message.get("roomName");
            String inviteCode = (String) message.get("inviteCode");
            
            messagingTemplate.convertAndSend("/topic/battle", Map.of(
                "type", "ROOM_CREATED",
                "roomName", roomName,
                "inviteCode", inviteCode,
                "message", "방 생성 성공"
            ));
            
            System.out.println("방 생성 응답 전송 완료");
            
        } catch (Exception e) {
            System.err.println("방 생성 실패: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
