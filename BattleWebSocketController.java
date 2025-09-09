package com.javabattle.arena.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import com.javabattle.arena.config.JwtUtil;
import com.javabattle.arena.service.BattleService;
import com.javabattle.arena.model.BattleRoom;
import java.util.Map;

@Controller
public class BattleWebSocketController {
    
    @Autowired
    private BattleService battleService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    
    @MessageMapping("/battle/create-room")
    public void createRoom(@Payload Map<String, Object> message, 
                          @Header("Authorization") String token) {
        System.out.println("=== create-room 메시지 수신 ===");
        System.out.println("메시지: " + message);
        System.out.println("토큰: " + token);
        
        try {
            String playerId = getUserIdFromToken(token);
            System.out.println("플레이어 ID: " + playerId);
            
            if (playerId == null) {
                System.err.println("플레이어 ID를 추출할 수 없습니다.");
                return;
            }
            
            String roomName = (String) message.get("roomName");
            String inviteCode = (String) message.get("inviteCode");
            
            System.out.println("방 이름: " + roomName + ", 초대코드: " + inviteCode);
            
            BattleRoom room = battleService.createRoom(roomName, playerId, inviteCode);
            System.out.println("방 생성 완료: " + room.getRoomId());
            
            messagingTemplate.convertAndSendToUser(playerId, "/queue/battle",
                Map.of(
                    "type", "ROOM_CREATED",
                    "roomId", room.getRoomId(),
                    "roomName", room.getRoomName(),
                    "inviteCode", inviteCode,
                    "message", "방이 생성되었습니다."
                ));
            
            System.out.println("응답 메시지 전송 완료");
                
        } catch (Exception e) {
            System.err.println("방 생성 실패: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @MessageMapping("/battle/join-by-code")
    public void joinByCode(@Payload Map<String, Object> message,
                          @Header("Authorization") String token) {
        System.out.println("=== join-by-code 메시지 수신 ===");
        try {
            String playerId = getUserIdFromToken(token);
            String inviteCode = (String) message.get("inviteCode");
            
            System.out.println("플레이어 ID: " + playerId + ", 초대코드: " + inviteCode);
            
            BattleRoom room = battleService.findRoomByInviteCode(inviteCode);
            
            if (room == null) {
                messagingTemplate.convertAndSendToUser(playerId, "/queue/battle",
                    Map.of("type", "ROOM_NOT_FOUND", "message", "방을 찾을 수 없습니다."));
                return;
            }
            
            if (room.isFull()) {
                messagingTemplate.convertAndSendToUser(playerId, "/queue/battle",
                    Map.of("type", "ROOM_FULL", "message", "방이 가득 찼습니다."));
                return;
            }
            
            boolean success = battleService.joinRoom(room.getRoomId(), playerId);
            
            if (success) {
                messagingTemplate.convertAndSendToUser(playerId, "/queue/battle",
                    Map.of(
                        "type", "ROOM_JOINED",
                        "roomId", room.getRoomId(),
                        "message", "방에 입장했습니다."
                    ));
            }
            
        } catch (Exception e) {
            System.err.println("초대코드 방 참가 실패: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @MessageMapping("/battle/join-random")
    public void joinRandomBattle(@Header("Authorization") String token) {
        System.out.println("=== join-random 메시지 수신 ===");
        try {
            String playerId = getUserIdFromToken(token);
            System.out.println("랜덤 매칭 플레이어: " + playerId);
            battleService.joinRandomQueue(playerId);
            
        } catch (Exception e) {
            System.err.println("랜덤 매칭 실패: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @MessageMapping("/battle/code-update")
    public void updateCode(@Payload Map<String, Object> message,
                          @Header("Authorization") String token) {
        try {
            String playerId = getUserIdFromToken(token);
            String roomId = (String) message.get("roomId");
            String code = (String) message.get("code");
            
            battleService.updateCode(roomId, playerId, code);
            
        } catch (Exception e) {
            System.err.println("코드 업데이트 실패: " + e.getMessage());
        }
    }
    
    @MessageMapping("/battle/submit")
    public void submitCode(@Payload Map<String, Object> message,
                          @Header("Authorization") String token) {
        try {
            String playerId = getUserIdFromToken(token);
            String roomId = (String) message.get("roomId");
            String code = (String) message.get("code");
            
            battleService.submitCode(roomId, playerId, code);
            
        } catch (Exception e) {
            System.err.println("코드 제출 실패: " + e.getMessage());
        }
    }
    
    @MessageMapping("/battle/leave")
    public void leaveRoom(@Payload Map<String, Object> message,
                         @Header("Authorization") String token) {
        try {
            String playerId = getUserIdFromToken(token);
            String roomId = (String) message.get("roomId");
            
            battleService.leaveRoom(roomId, playerId);
            
        } catch (Exception e) {
            System.err.println("방 나가기 실패: " + e.getMessage());
        }
    }
    
    private String getUserIdFromToken(String token) {
        try {
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
            
            Long userId = jwtUtil.extractUserId(token);
            return userId != null ? userId.toString() : null;
            
        } catch (Exception e) {
            System.err.println("토큰에서 사용자 ID 추출 실패: " + e.getMessage());
            return null;
        }
    }
}

    @MessageMapping("/battle/test")
    public void testMessage(@Payload Map<String, Object> message) {
        System.out.println("테스트 메시지 수신: " + message);
        messagingTemplate.convertAndSend("/topic/test", 
            Map.of("type", "TEST_RESPONSE", "message", "서버 응답 테스트"));
    }
