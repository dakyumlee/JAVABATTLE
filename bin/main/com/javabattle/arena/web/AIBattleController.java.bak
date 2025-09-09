package com.javabattle.arena.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import com.javabattle.arena.service.AIBattleService;
import com.javabattle.arena.config.JwtUtil;

import java.util.Map;

@Controller
public class AIBattleController {
    
    @Autowired
    private AIBattleService aiBattleService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @MessageMapping("/ai-battle/join")
    public void joinAIBattle(@Payload Map<String, Object> message, SimpMessageHeaderAccessor headerAccessor) {
        String token = extractTokenFromHeaders(headerAccessor);
        if (token != null) {
            try {
                Long userId = jwtUtil.extractUserId(token);
                if (userId != null) {
                    String difficulty = (String) message.getOrDefault("difficulty", "easy");
                    aiBattleService.joinQueue(String.valueOf(userId), difficulty);
                }
            } catch (Exception e) {
                System.out.println("Token validation failed: " + e.getMessage());
            }
        }
    }
    
    @MessageMapping("/ai-battle/code-update")
    public void updateCode(@Payload Map<String, Object> message, SimpMessageHeaderAccessor headerAccessor) {
        String token = extractTokenFromHeaders(headerAccessor);
        if (token != null) {
            try {
                Long userId = jwtUtil.extractUserId(token);
                if (userId != null) {
                    String code = (String) message.get("code");
                    aiBattleService.updateCode(String.valueOf(userId), code);
                }
            } catch (Exception e) {
                System.out.println("Token validation failed: " + e.getMessage());
            }
        }
    }
    
    @MessageMapping("/ai-battle/submit")
    public void submitCode(@Payload Map<String, Object> message, SimpMessageHeaderAccessor headerAccessor) {
        String token = extractTokenFromHeaders(headerAccessor);
        if (token != null) {
            try {
                Long userId = jwtUtil.extractUserId(token);
                if (userId != null) {
                    String code = (String) message.get("code");
                    aiBattleService.submitCode(String.valueOf(userId), code);
                }
            } catch (Exception e) {
                System.out.println("Token validation failed: " + e.getMessage());
            }
        }
    }
    
    @MessageMapping("/ai-battle/leave")
    public void leaveBattle(@Payload Map<String, Object> message, SimpMessageHeaderAccessor headerAccessor) {
        String token = extractTokenFromHeaders(headerAccessor);
        if (token != null) {
            try {
                Long userId = jwtUtil.extractUserId(token);
                if (userId != null) {
                    aiBattleService.leaveQueue(String.valueOf(userId));
                }
            } catch (Exception e) {
                System.out.println("Token validation failed: " + e.getMessage());
            }
        }
    }
    
    @GetMapping("/api/ai-battle/stats")
    @ResponseBody
    public Map<String, Object> getBattleStats(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            Long userId = jwtUtil.extractUserId(token);
            if (userId != null) {
                return aiBattleService.getBattleStatistics(String.valueOf(userId));
            }
            return Map.of("error", "Invalid token");
        } catch (Exception e) {
            return Map.of("error", "Unauthorized");
        }
    }
    
    private String extractTokenFromHeaders(SimpMessageHeaderAccessor headerAccessor) {
        Object authHeader = headerAccessor.getFirstNativeHeader("Authorization");
        if (authHeader != null) {
            String authHeaderStr = authHeader.toString();
            if (authHeaderStr.startsWith("Bearer ")) {
                return authHeaderStr.substring(7);
            }
        }
        return null;
    }
}
