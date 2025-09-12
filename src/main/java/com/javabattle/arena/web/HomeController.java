package com.javabattle.arena.web;

import com.javabattle.arena.service.ProblemService;
import com.javabattle.arena.service.CodeExecutionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Controller
public class HomeController {

    @Autowired
    private ProblemService problemService;
    
    @Autowired
    private CodeExecutionService codeExecutionService;
    
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    
    private final Map<String, GameRoom> rooms = new ConcurrentHashMap<>();

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/admin")
    public String admin() {
        return "admin";
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
    public String battle() {
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

    @MessageMapping("/battle/create-room")
    public void createRoom(@Payload Map<String, String> payload, SimpMessageHeaderAccessor headerAccessor) {
        System.out.println("=== 핸들러 호출됨! ===");
        System.out.println("=== 방 생성 요청 받음 ===");
        System.out.println("메시지: " + payload);
        
        try {
            String roomName = payload.get("roomName");
            String inviteCode = payload.get("inviteCode");
            String username = getUsernameFromHeader(headerAccessor);
            Map<String, Object> problemMap = generateProblemSafely();
            
            GameRoom room = new GameRoom();
            room.setRoomId("ROOM_" + System.currentTimeMillis());
            room.setRoomName(roomName);
            room.setInviteCode(inviteCode);
            room.setHost(username);
            room.setProblemMap(problemMap);
            room.setOpponents(Arrays.asList("AI 상대방"));
            
            rooms.put(inviteCode, room);
            
            Map<String, Object> response = new HashMap<>();
            response.put("type", "GAME_START");
            response.put("roomId", room.getRoomId());
            response.put("problem", Map.of(
                "title", problemMap.get("title"),
                "description", problemMap.get("description")
            ));
            response.put("opponents", room.getOpponents());
            
            System.out.println("=== 응답 전송 시도 ===");
            System.out.println("응답 내용: " + response);
            
            messagingTemplate.convertAndSend("/topic/battle", response);
            
        } catch (Exception e) {
            System.out.println("방 생성 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
            sendErrorResponse("방 생성에 실패했습니다.");
        }
    }
    
    @MessageMapping("/battle/join-by-code")
    public void joinByCode(@Payload Map<String, String> payload, SimpMessageHeaderAccessor headerAccessor) {
        System.out.println("=== 초대코드로 참가 요청 ===");
        System.out.println("메시지: " + payload);
        
        try {
            String inviteCode = payload.get("inviteCode");
            String username = getUsernameFromHeader(headerAccessor);
            
            GameRoom room = rooms.get(inviteCode);
            if (room == null) {
                System.out.println("방이 없어서 새로 생성합니다.");
                Map<String, Object> problemMap = generateProblemSafely();
                
                room = new GameRoom();
                room.setRoomId("ROOM_" + System.currentTimeMillis());
                room.setRoomName("참가방");
                room.setInviteCode(inviteCode);
                room.setHost(username);
                room.setProblemMap(problemMap);
                room.setOpponents(Arrays.asList("AI 상대방"));
                
                rooms.put(inviteCode, room);
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("type", "GAME_JOIN");
            response.put("roomId", room.getRoomId());
            response.put("problem", Map.of(
                "title", room.getProblemMap().get("title"),
                "description", room.getProblemMap().get("description")
            ));
            response.put("opponents", room.getOpponents());
            
            messagingTemplate.convertAndSend("/topic/battle", response);
            
        } catch (Exception e) {
            System.out.println("방 참가 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
            sendErrorResponse("방 참가에 실패했습니다.");
        }
    }

    @MessageMapping("/battle/join-random")
    public void joinRandom(@Payload Map<String, Object> message, SimpMessageHeaderAccessor headerAccessor) {
        System.out.println("=== 랜덤 매칭 요청 ===");
        System.out.println("메시지: " + message);
        
        try {
            String username = getUsernameFromHeader(headerAccessor);
            String roomId = "RANDOM_ROOM_" + System.currentTimeMillis();
            Map<String, Object> problemMap = generateProblemSafely();
            
            GameRoom room = new GameRoom();
            room.setRoomId(roomId);
            room.setRoomName("랜덤매칭");
            room.setInviteCode(roomId);
            room.setHost(username);
            room.setProblemMap(problemMap);
            room.setOpponents(Arrays.asList("랜덤 상대방"));
            
            rooms.put(roomId, room);
            
            Map<String, Object> response = new HashMap<>();
            response.put("type", "GAME_START");
            response.put("roomId", roomId);
            response.put("problem", Map.of(
                "title", problemMap.get("title"),
                "description", problemMap.get("description")
            ));
            response.put("opponents", room.getOpponents());
            
            messagingTemplate.convertAndSend("/topic/battle", response);
            
        } catch (Exception e) {
            System.out.println("랜덤 매칭 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
            sendErrorResponse("랜덤 매칭에 실패했습니다.");
        }
    }

    @MessageMapping("/battle/code-update")
    public void updateCode(@Payload Map<String, Object> message) {
        System.out.println("코드 업데이트: " + message.get("roomId"));
    }

    @MessageMapping("/battle/submit")
    public void submitCode(@Payload Map<String, Object> message, SimpMessageHeaderAccessor headerAccessor) {
        System.out.println("=== 코드 제출 ===");
        
        try {
            String roomId = (String) message.get("roomId");
            String code = (String) message.get("code");
            
            GameRoom room = rooms.get(roomId);
            if (room == null) {
                sendErrorResponse("유효하지 않은 방입니다.");
                return;
            }
            
            boolean isCorrect = validateCode(code, room.getProblemMap());
            Map<String, Object> response = new HashMap<>();
            
            if (isCorrect) {
                response.put("type", "GAME_END");
                response.put("result", "WIN");
                response.put("message", "정답입니다!");
                rooms.remove(roomId);
            } else {
                response.put("type", "INCORRECT");
                response.put("message", "틀렸습니다. 다시 시도하세요.");
            }
            
            messagingTemplate.convertAndSend("/topic/battle", response);
            
        } catch (Exception e) {
            System.out.println("코드 제출 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
            sendErrorResponse("코드 제출에 실패했습니다.");
        }
    }

    @PostMapping("/api/battle/run")
    @ResponseBody
    public Map<String, Object> runCode(@RequestBody Map<String, String> request) {
        String code = request.get("code");
        String input = request.getOrDefault("input", "");
        
        CodeExecutionService.CodeExecutionResult result = codeExecutionService.executeJavaCode(code, input);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", result.isSuccess());
        
        if (result.isSuccess()) {
            response.put("output", result.getOutput());
        } else {
            response.put("error", result.getError());
        }
        
        return response;
    }

    @MessageMapping("/battle/leave")
    public void leaveRoom(@Payload Map<String, Object> message) {
        String roomId = (String) message.get("roomId");
        rooms.remove(roomId);
        System.out.println("방 나가기: " + roomId);
    }

    private String getUsernameFromHeader(SimpMessageHeaderAccessor headerAccessor) {
        if (headerAccessor.getUser() != null) {
            return headerAccessor.getUser().getName();
        } else {
            return "익명사용자_" + System.currentTimeMillis();
        }
    }
    
    private Map<String, Object> generateProblemSafely() {
        try {
            return problemService.generateRandomProblem();
        } catch (Exception e) {
            System.out.println("문제 생성 실패, 기본 문제 사용: " + e.getMessage());
            return createDefaultProblem();
        }
    }
    
    private Map<String, Object> createDefaultProblem() {
        Map<String, Object> problem = new HashMap<>();
        problem.put("title", "기본 문제 - 두 수의 합");
        problem.put("description", "두 개의 정수 a, b를 입력받아 합을 출력하는 프로그램을 작성하세요.\n\n입력:\n첫 줄에 두 정수 a, b가 공백으로 구분되어 주어집니다.\n\n출력:\na + b의 결과를 출력합니다.\n\n예시:\n입력: 3 5\n출력: 8");
        return problem;
    }
    
    private boolean validateCode(String code, Map<String, Object> problem) {
        return code != null && code.contains("Scanner") && code.contains("System.out");
    }
    
    private void sendErrorResponse(String message) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("type", "ERROR");
        errorResponse.put("message", message);
        messagingTemplate.convertAndSend("/topic/battle", errorResponse);
    }
    
    public static class GameRoom {
        private String roomId;
        private String roomName;
        private String inviteCode;
        private String host;
        private Map<String, Object> problemMap;
        private List<String> opponents;
        
        public String getRoomId() { return roomId; }
        public void setRoomId(String roomId) { this.roomId = roomId; }
        
        public String getRoomName() { return roomName; }
        public void setRoomName(String roomName) { this.roomName = roomName; }
        
        public String getInviteCode() { return inviteCode; }
        public void setInviteCode(String inviteCode) { this.inviteCode = inviteCode; }
        
        public String getHost() { return host; }
        public void setHost(String host) { this.host = host; }
        
        public Map<String, Object> getProblemMap() { return problemMap; }
        public void setProblemMap(Map<String, Object> problemMap) { this.problemMap = problemMap; }
        
        public List<String> getOpponents() { return opponents; }
        public void setOpponents(List<String> opponents) { this.opponents = opponents; }
    }
}