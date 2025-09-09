package com.javabattle.arena.model;

import java.time.LocalDateTime;
import java.util.*;

public class BattleRoom {
    private String roomId;
    private String roomName;
    private String creatorId;
    private String inviteCode;
    private List<String> players;
    private BattleStatus status;
    private Problem problem;
    private Map<String, String> playerCodes;
    private LocalDateTime createdAt;
    private LocalDateTime startedAt;
    
    public BattleRoom() {
        this.players = new ArrayList<>();
        this.playerCodes = new HashMap<>();
        this.createdAt = LocalDateTime.now();
        this.status = BattleStatus.WAITING;
    }
    
    public void addPlayer(String playerId) {
        if (players.size() < 2) {
            players.add(playerId);
            playerCodes.put(playerId, "");
        }
    }
    
    public void removePlayer(String playerId) {
        players.remove(playerId);
        playerCodes.remove(playerId);
    }
    
    public void updatePlayerCode(String playerId, String code) {
        playerCodes.put(playerId, code);
    }
    
    public boolean isFull() {
        return players.size() >= 2;
    }
    
    public boolean isEmpty() {
        return players.isEmpty();
    }
    
    public String getRoomId() { return roomId; }
    public void setRoomId(String roomId) { this.roomId = roomId; }
    
    public String getRoomName() { return roomName; }
    public void setRoomName(String roomName) { this.roomName = roomName; }
    
    public String getCreatorId() { return creatorId; }
    public void setCreatorId(String creatorId) { this.creatorId = creatorId; }
    
    public String getInviteCode() { return inviteCode; }
    public void setInviteCode(String inviteCode) { this.inviteCode = inviteCode; }
    
    public List<String> getPlayers() { return players; }
    public void setPlayers(List<String> players) { this.players = players; }
    
    public BattleStatus getStatus() { return status; }
    public void setStatus(BattleStatus status) { this.status = status; }
    
    public Problem getProblem() { return problem; }
    public void setProblem(Problem problem) { this.problem = problem; }
    
    public Map<String, String> getPlayerCodes() { return playerCodes; }
    public void setPlayerCodes(Map<String, String> playerCodes) { this.playerCodes = playerCodes; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getStartedAt() { return startedAt; }
    public void setStartedAt(LocalDateTime startedAt) { this.startedAt = startedAt; }
}

enum BattleStatus {
    WAITING,
    IN_PROGRESS,
    FINISHED
}
