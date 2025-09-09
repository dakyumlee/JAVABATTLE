package com.javabattle.arena.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.Model;
import java.util.Map;

@Controller
public class HomeController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/study")
    public String study() {
        return "study";
    }

    @GetMapping("/ai-tutor")
    public String aiTutor() {
        return "ai-tutor";
    }

    @GetMapping("/review")
    public String review() {
        return "review";
    }

    @GetMapping("/battle")
    public String battle(@RequestParam(required = false) String mode,
                        @RequestParam(required = false) String room,
                        @RequestParam(required = false) String code,
                        Model model) {
        System.out.println("배틀 페이지 접근 - 모드: " + mode + ", 방이름: " + room);
        model.addAttribute("mode", mode != null ? mode : "random");
        model.addAttribute("roomName", room);
        model.addAttribute("inviteCode", code);
        return "battle";
    }

    @GetMapping("/teacher")
    public String teacher() {
        return "teacher";
    }

    @GetMapping("/teacher-stats")
    public String teacherStats() {
        return "statistics";
    }

    @GetMapping("/teacher-materials")
    public String teacherMaterials() {
        return "teacher-materials";
    }

    @MessageMapping("/test")
    public void handleTest(@Payload String message) {
        System.out.println("=== 테스트 메시지 수신 ===");
        System.out.println("받은 메시지: " + message);
        
        try {
            messagingTemplate.convertAndSend("/topic/test", "서버에서 응답: " + message);
            System.out.println("응답 전송 완료");
        } catch (Exception e) {
            System.err.println("응답 전송 실패: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @MessageMapping("/battle/create-room")
    public void createBattleRoom(@Payload Map<String, Object> message) {
        System.out.println("=== 배틀 방 생성 요청 ===");
        System.out.println("받은 데이터: " + message);
        
        try {
            String roomName = (String) message.get("roomName");
            String inviteCode = (String) message.get("inviteCode");
            
            System.out.println("방 이름: " + roomName);
            System.out.println("초대코드: " + inviteCode);
            
            Map<String, Object> response = Map.of(
                "type", "ROOM_CREATED",
                "roomName", roomName,
                "inviteCode", inviteCode,
                "roomId", "room_" + System.currentTimeMillis(),
                "message", "방이 성공적으로 생성되었습니다"
            );
            
            messagingTemplate.convertAndSend("/topic/battle", response);
            System.out.println("방 생성 응답 전송 완료");
            
        } catch (Exception e) {
            System.err.println("방 생성 처리 중 오류: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

    @MessageMapping("/battle/join-by-code")
    public void joinByCode(@Payload Map<String, Object> message) {
        System.out.println("=== 초대코드로 방 참가 요청 ===");
        System.out.println("받은 데이터: " + message);
        
        try {
            String inviteCode = (String) message.get("inviteCode");
            System.out.println("초대코드: " + inviteCode);
            
            // 간단한 응답 (실제 방 검색 로직은 나중에 구현)
            Map<String, Object> response = Map.of(
                "type", "ROOM_NOT_FOUND",
                "message", "해당 초대코드의 방을 찾을 수 없습니다.",
                "inviteCode", inviteCode
            );
            
            messagingTemplate.convertAndSend("/topic/battle", response);
            System.out.println("초대코드 응답 전송 완료");
            
        } catch (Exception e) {
            System.err.println("초대코드 처리 중 오류: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @MessageMapping("/battle/join-random")
    public void joinRandom(@Payload Map<String, Object> message) {
        System.out.println("=== 랜덤 매칭 요청 ===");
        System.out.println("받은 데이터: " + message);
        
        try {
            Map<String, Object> response = Map.of(
                "type", "WAITING",
                "message", "상대방을 찾는 중입니다..."
            );
            
            messagingTemplate.convertAndSend("/topic/battle", response);
            System.out.println("랜덤 매칭 응답 전송 완료");
            
        } catch (Exception e) {
            System.err.println("랜덤 매칭 처리 중 오류: " + e.getMessage());
            e.printStackTrace();
        }
    }
