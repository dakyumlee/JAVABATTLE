package com.javabattle.arena.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import com.javabattle.arena.model.*;
import com.javabattle.arena.repository.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AIBattleService {
    
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    
    @Autowired
    private UserRepository userRepository;
    
    private final Queue<String> waitingQueue = new LinkedList<>();
    private final Map<String, AIBattleRoom> activeRooms = new ConcurrentHashMap<>();
    private final Map<String, String> playerRoomMap = new ConcurrentHashMap<>();
    
    public static class AIGeneratedProblem {
        private String title;
        private String description;
        private String difficulty;
        private String sampleInput;
        private String sampleOutput;
        private String expectedSolution;
        private List<String> testCases;
        
        public AIGeneratedProblem(String title, String description, String difficulty) {
            this.title = title;
            this.description = description;
            this.difficulty = difficulty;
            this.testCases = new ArrayList<>();
        }
        
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public String getDifficulty() { return difficulty; }
        public void setDifficulty(String difficulty) { this.difficulty = difficulty; }
        public String getSampleInput() { return sampleInput; }
        public void setSampleInput(String sampleInput) { this.sampleInput = sampleInput; }
        public String getSampleOutput() { return sampleOutput; }
        public void setSampleOutput(String sampleOutput) { this.sampleOutput = sampleOutput; }
        public String getExpectedSolution() { return expectedSolution; }
        public void setExpectedSolution(String expectedSolution) { this.expectedSolution = expectedSolution; }
        public List<String> getTestCases() { return testCases; }
        public void setTestCases(List<String> testCases) { this.testCases = testCases; }
    }
    
    public static class AIBattleRoom {
        private String roomId;
        private String player1Id;
        private String player2Id;
        private AIGeneratedProblem problem;
        private LocalDateTime startTime;
        private boolean isActive;
        private Map<String, String> playerCodes;
        private Map<String, Boolean> submissions;
        private Map<String, Integer> scores;
        private String difficulty;
        
        public AIBattleRoom(String roomId, String player1Id, String player2Id, String difficulty) {
            this.roomId = roomId;
            this.player1Id = player1Id;
            this.player2Id = player2Id;
            this.difficulty = difficulty;
            this.startTime = LocalDateTime.now();
            this.isActive = true;
            this.playerCodes = new ConcurrentHashMap<>();
            this.submissions = new ConcurrentHashMap<>();
            this.scores = new ConcurrentHashMap<>();
            this.scores.put(player1Id, 0);
            this.scores.put(player2Id, 0);
        }
        
        public String getRoomId() { return roomId; }
        public String getPlayer1Id() { return player1Id; }
        public String getPlayer2Id() { return player2Id; }
        public AIGeneratedProblem getProblem() { return problem; }
        public void setProblem(AIGeneratedProblem problem) { this.problem = problem; }
        public LocalDateTime getStartTime() { return startTime; }
        public boolean isActive() { return isActive; }
        public void setActive(boolean active) { this.isActive = active; }
        public Map<String, String> getPlayerCodes() { return playerCodes; }
        public Map<String, Boolean> getSubmissions() { return submissions; }
        public Map<String, Integer> getScores() { return scores; }
        public String getDifficulty() { return difficulty; }
        
        public String getOpponentId(String playerId) {
            return playerId.equals(player1Id) ? player2Id : player1Id;
        }
    }
    
    public void joinQueue(String playerId, String difficulty) {
        synchronized (waitingQueue) {
            String queueKey = playerId + ":" + difficulty;
            if (!waitingQueue.contains(queueKey)) {
                waitingQueue.offer(queueKey);
                sendWaitingMessage(playerId);
                tryMatchPlayers(difficulty);
            }
        }
    }
    
    private void tryMatchPlayers(String difficulty) {
        List<String> playersWithDifficulty = new ArrayList<>();
        synchronized (waitingQueue) {
            Iterator<String> iterator = waitingQueue.iterator();
            while (iterator.hasNext()) {
                String queueEntry = iterator.next();
                if (queueEntry.endsWith(":" + difficulty)) {
                    playersWithDifficulty.add(queueEntry.split(":")[0]);
                    iterator.remove();
                    if (playersWithDifficulty.size() == 2) break;
                }
            }
        }
        
        if (playersWithDifficulty.size() >= 2) {
            createAIBattleRoom(playersWithDifficulty.get(0), playersWithDifficulty.get(1), difficulty);
        }
    }
    
    private void createAIBattleRoom(String player1Id, String player2Id, String difficulty) {
        String roomId = UUID.randomUUID().toString();
        AIBattleRoom room = new AIBattleRoom(roomId, player1Id, player2Id, difficulty);
        
        AIGeneratedProblem problem = generateAIProblem(difficulty);
        room.setProblem(problem);
        
        activeRooms.put(roomId, room);
        playerRoomMap.put(player1Id, roomId);
        playerRoomMap.put(player2Id, roomId);
        
        startAIGame(room);
    }
    
    private AIGeneratedProblem generateAIProblem(String difficulty) {
        return createFallbackProblem(difficulty);
    }
    
    private AIGeneratedProblem createFallbackProblem(String difficulty) {
        AIGeneratedProblem problem = new AIGeneratedProblem("", "", difficulty);
        
        switch (difficulty.toLowerCase()) {
            case "easy":
                problem.setTitle("두 수의 합");
                problem.setDescription("두 정수 A, B를 입력받아 A+B를 출력하는 프로그램을 작성하시오.");
                problem.setSampleInput("3 5");
                problem.setSampleOutput("8");
                break;
            case "medium":
                problem.setTitle("배열의 최댓값");
                problem.setDescription("N개의 정수가 주어졌을 때, 가장 큰 값을 출력하는 프로그램을 작성하시오.");
                problem.setSampleInput("5\n1 3 5 2 4");
                problem.setSampleOutput("5");
                break;
            case "hard":
                problem.setTitle("피보나치 수열");
                problem.setDescription("N번째 피보나치 수를 출력하는 프로그램을 작성하시오.");
                problem.setSampleInput("10");
                problem.setSampleOutput("55");
                break;
            default:
                problem.setTitle("두 수의 합");
                problem.setDescription("두 정수 A, B를 입력받아 A+B를 출력하는 프로그램을 작성하시오.");
                problem.setSampleInput("3 5");
                problem.setSampleOutput("8");
                break;
        }
        
        return problem;
    }
    
    private void startAIGame(AIBattleRoom room) {
        Map<String, Object> gameStart = new HashMap<>();
        gameStart.put("type", "GAME_START");
        gameStart.put("roomId", room.getRoomId());
        
        Map<String, Object> problemMap = new HashMap<>();
        problemMap.put("title", room.getProblem().getTitle());
        problemMap.put("description", room.getProblem().getDescription());
        problemMap.put("sampleInput", room.getProblem().getSampleInput());
        problemMap.put("sampleOutput", room.getProblem().getSampleOutput());
        problemMap.put("difficulty", room.getProblem().getDifficulty());
        gameStart.put("problem", problemMap);
        
        Optional<User> player1 = userRepository.findById(Long.parseLong(room.getPlayer1Id()));
        Optional<User> player2 = userRepository.findById(Long.parseLong(room.getPlayer2Id()));
        
        if (player1.isPresent() && player2.isPresent()) {
            gameStart.put("opponent", Map.of("name", player2.get().getNickname()));
            messagingTemplate.convertAndSendToUser(room.getPlayer1Id(), "/queue/battle", gameStart);
            
            gameStart.put("opponent", Map.of("name", player1.get().getNickname()));
            messagingTemplate.convertAndSendToUser(room.getPlayer2Id(), "/queue/battle", gameStart);
        }
    }
    
    public void updateCode(String playerId, String code) {
        String roomId = playerRoomMap.get(playerId);
        if (roomId != null) {
            AIBattleRoom room = activeRooms.get(roomId);
            if (room != null && room.isActive()) {
                room.getPlayerCodes().put(playerId, code);
                
                String opponentId = room.getOpponentId(playerId);
                Map<String, Object> codeUpdate = new HashMap<>();
                codeUpdate.put("type", "CODE_UPDATE");
                codeUpdate.put("code", code);
                
                messagingTemplate.convertAndSendToUser(opponentId, "/queue/battle", codeUpdate);
            }
        }
    }
    
    public void submitCode(String playerId, String code) {
        String roomId = playerRoomMap.get(playerId);
        if (roomId != null) {
            AIBattleRoom room = activeRooms.get(roomId);
            if (room != null && room.isActive()) {
                
                boolean isCorrect = checkCodeSolution(code, room.getProblem());
                
                if (isCorrect) {
                    room.getScores().put(playerId, room.getScores().get(playerId) + 1);
                    endAIGame(room, playerId, "WIN");
                } else {
                    Map<String, Object> incorrectResult = new HashMap<>();
                    incorrectResult.put("type", "INCORRECT");
                    incorrectResult.put("result", "INCORRECT");
                    incorrectResult.put("message", "틀렸습니다. 다시 시도해보세요.");
                    messagingTemplate.convertAndSendToUser(playerId, "/queue/battle", incorrectResult);
                }
            }
        }
    }
    
    private boolean checkCodeSolution(String code, AIGeneratedProblem problem) {
        if (code == null || code.trim().isEmpty()) {
            return false;
        }
        
        boolean hasMainMethod = code.contains("public static void main");
        boolean hasSystemOut = code.contains("System.out.print");
        boolean hasScanner = code.contains("Scanner") || code.contains("BufferedReader");
        
        switch (problem.getDifficulty().toLowerCase()) {
            case "easy":
                return hasMainMethod && hasSystemOut && code.length() > 50;
            case "medium":
                return hasMainMethod && hasSystemOut && hasScanner && code.length() > 100;
            case "hard":
                return hasMainMethod && hasSystemOut && hasScanner && code.length() > 150;
            default:
                return hasMainMethod && hasSystemOut;
        }
    }
    
    private void endAIGame(AIBattleRoom room, String winnerId, String result) {
        room.setActive(false);
        
        String loserId = room.getOpponentId(winnerId);
        
        Map<String, Object> winMessage = new HashMap<>();
        winMessage.put("type", "GAME_END");
        winMessage.put("result", "WIN");
        winMessage.put("aiGenerated", true);
        winMessage.put("difficulty", room.getDifficulty());
        messagingTemplate.convertAndSendToUser(winnerId, "/queue/battle", winMessage);
        
        Map<String, Object> loseMessage = new HashMap<>();
        loseMessage.put("type", "GAME_END");
        loseMessage.put("result", "LOSE");
        loseMessage.put("aiGenerated", true);
        loseMessage.put("difficulty", room.getDifficulty());
        messagingTemplate.convertAndSendToUser(loserId, "/queue/battle", loseMessage);
        
        cleanupRoom(room.getRoomId());
    }
    
    public void leaveQueue(String playerId) {
        synchronized (waitingQueue) {
            waitingQueue.removeIf(entry -> entry.startsWith(playerId + ":"));
        }
        
        String roomId = playerRoomMap.get(playerId);
        if (roomId != null) {
            AIBattleRoom room = activeRooms.get(roomId);
            if (room != null) {
                String opponentId = room.getOpponentId(playerId);
                
                Map<String, Object> leaveMessage = new HashMap<>();
                leaveMessage.put("type", "OPPONENT_LEFT");
                messagingTemplate.convertAndSendToUser(opponentId, "/queue/battle", leaveMessage);
                
                cleanupRoom(roomId);
            }
        }
    }
    
    private void cleanupRoom(String roomId) {
        AIBattleRoom room = activeRooms.remove(roomId);
        if (room != null) {
            playerRoomMap.remove(room.getPlayer1Id());
            playerRoomMap.remove(room.getPlayer2Id());
        }
    }
    
    private void sendWaitingMessage(String playerId) {
        Map<String, Object> waiting = new HashMap<>();
        waiting.put("type", "WAITING");
        waiting.put("message", "AI가 문제를 준비하고 있습니다...");
        messagingTemplate.convertAndSendToUser(playerId, "/queue/battle", waiting);
    }
    
    public Map<String, Object> getBattleStatistics(String playerId) {
        Map<String, Object> stats = new HashMap<>();
        stats.put("easyWins", 0);
        stats.put("mediumWins", 0);
        stats.put("hardWins", 0);
        stats.put("totalBattles", 0);
        return stats;
    }
}
