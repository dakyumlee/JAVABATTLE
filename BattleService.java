package com.javabattle.arena.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import com.javabattle.arena.model.BattleRoom;
import com.javabattle.arena.model.Problem;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.time.LocalDateTime;

@Service
public class BattleService {
    
    private final Map<String, BattleRoom> battleRooms = new ConcurrentHashMap<>();
    private final Map<String, String> inviteCodeToRoomId = new ConcurrentHashMap<>();
    private final Queue<String> waitingPlayers = new ConcurrentLinkedQueue<>();
    
    @Autowired
    private ProblemService problemService;
    
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    
    public BattleRoom createRoom(String roomName, String creatorId, String inviteCode) {
        String roomId = UUID.randomUUID().toString();
        BattleRoom room = new BattleRoom();
        room.setRoomId(roomId);
        room.setRoomName(roomName);
        room.setCreatorId(creatorId);
        room.setInviteCode(inviteCode);
        room.setStatus(BattleStatus.WAITING);
        room.setCreatedAt(LocalDateTime.now());
        
        battleRooms.put(roomId, room);
        inviteCodeToRoomId.put(inviteCode, roomId);
        
        joinRoom(roomId, creatorId);
        
        return room;
    }
    
    public BattleRoom findRoomByInviteCode(String inviteCode) {
        String roomId = inviteCodeToRoomId.get(inviteCode);
        if (roomId != null) {
            return battleRooms.get(roomId);
        }
        return null;
    }
    
    public boolean joinRoom(String roomId, String playerId) {
        BattleRoom room = battleRooms.get(roomId);
        if (room == null || room.getPlayers().size() >= 2) {
            return false;
        }
        
        room.addPlayer(playerId);
        
        messagingTemplate.convertAndSend("/topic/room/" + roomId, 
            Map.of("type", "PLAYER_JOINED", "playerId", playerId, "room", room));
        
        if (room.getPlayers().size() == 2) {
            startGame(room);
        }
        
        return true;
    }
    
    public void joinRandomQueue(String playerId) {
        String waitingPlayer = waitingPlayers.poll();
        if (waitingPlayer != null && !waitingPlayer.equals(playerId)) {
            BattleRoom room = createRoom("Random Battle", waitingPlayer, generateRandomCode());
            joinRoom(room.getRoomId(), playerId);
        } else {
            waitingPlayers.offer(playerId);
            messagingTemplate.convertAndSendToUser(playerId, "/queue/battle",
                Map.of("type", "WAITING", "message", "상대방을 찾는 중..."));
        }
    }
    
    private void startGame(BattleRoom room) {
        room.setStatus(BattleStatus.IN_PROGRESS);
        room.setStartedAt(LocalDateTime.now());
        
        Problem problem = createSampleProblem();
        room.setProblem(problem);
        
        for (String playerId : room.getPlayers()) {
            messagingTemplate.convertAndSendToUser(playerId, "/queue/battle",
                Map.of(
                    "type", "GAME_START",
                    "roomId", room.getRoomId(),
                    "problem", Map.of(
                        "title", problem.getTitle(),
                        "description", problem.getDescription()
                    ),
                    "opponents", getOpponents(room, playerId)
                ));
        }
        
        startTimer(room);
    }
    
    public void submitCode(String roomId, String playerId, String code) {
        BattleRoom room = battleRooms.get(roomId);
        if (room == null || room.getStatus() != BattleStatus.IN_PROGRESS) {
            return;
        }
        
        boolean isCorrect = validateCode(code, room.getProblem());
        
        if (isCorrect) {
            endGame(room, playerId, "CORRECT_ANSWER");
        } else {
            messagingTemplate.convertAndSendToUser(playerId, "/queue/battle",
                Map.of("type", "INCORRECT", "message", "틀렸습니다. 다시 시도하세요."));
        }
    }
    
    public void updateCode(String roomId, String playerId, String code) {
        BattleRoom room = battleRooms.get(roomId);
        if (room == null) return;
        
        room.updatePlayerCode(playerId, code);
        
        for (String player : room.getPlayers()) {
            if (!player.equals(playerId)) {
                messagingTemplate.convertAndSendToUser(player, "/queue/battle",
                    Map.of("type", "OPPONENT_CODE_UPDATE", "code", code));
            }
        }
    }
    
    public void leaveRoom(String roomId, String playerId) {
        BattleRoom room = battleRooms.get(roomId);
        if (room == null) return;
        
        room.removePlayer(playerId);
        
        if (room.getPlayers().isEmpty()) {
            battleRooms.remove(roomId);
            if (room.getInviteCode() != null) {
                inviteCodeToRoomId.remove(room.getInviteCode());
            }
        } else {
            for (String player : room.getPlayers()) {
                messagingTemplate.convertAndSendToUser(player, "/queue/battle",
                    Map.of("type", "OPPONENT_LEFT", "message", "상대방이 나갔습니다."));
            }
        }
    }
    
    private void endGame(BattleRoom room, String winnerId, String reason) {
        room.setStatus(BattleStatus.FINISHED);
        
        for (String playerId : room.getPlayers()) {
            String result = playerId.equals(winnerId) ? "WIN" : "LOSE";
            messagingTemplate.convertAndSendToUser(playerId, "/queue/battle",
                Map.of(
                    "type", "GAME_END",
                    "result", result,
                    "reason", reason,
                    "winner", winnerId
                ));
        }
        
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                battleRooms.remove(room.getRoomId());
                if (room.getInviteCode() != null) {
                    inviteCodeToRoomId.remove(room.getInviteCode());
                }
            }
        }, 5000);
    }
    
    private void startTimer(BattleRoom room) {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (room.getStatus() == BattleStatus.IN_PROGRESS) {
                    endGame(room, null, "TIME_UP");
                }
            }
        }, 5 * 60 * 1000);
    }
    
    private boolean validateCode(String code, Problem problem) {
        return code.contains("Scanner") && code.contains("System.out.println");
    }
    
    private List<String> getOpponents(BattleRoom room, String playerId) {
        return room.getPlayers().stream()
                .filter(p -> !p.equals(playerId))
                .collect(java.util.stream.Collectors.toList());
    }
    
    private Problem createSampleProblem() {
        Problem problem = new Problem();
        problem.setTitle("두 수의 합");
        problem.setDescription("두 정수 A와 B를 입력받은 다음, A+B를 출력하는 프로그램을 작성하시오.\n\n입력:\n첫째 줄에 A와 B가 주어진다. (0 < A, B < 10)\n\n출력:\n첫째 줄에 A+B를 출력한다.\n\n예제 입력:\n1 2\n\n예제 출력:\n3");
        return problem;
    }
    
    private String generateRandomCode() {
        return UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }
}
