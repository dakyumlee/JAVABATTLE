package com.javabattle.arena.util;

import com.javabattle.arena.config.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import jakarta.servlet.http.HttpServletRequest;

@Component
public class JwtUtils {
    
    @Autowired
    private JwtUtil jwtUtil;
    
    public String getUserIdFromRequest(HttpServletRequest request) {
        String token = extractTokenFromRequest(request);
        if (token != null) {
            Long userId = jwtUtil.extractUserId(token);
            return userId != null ? userId.toString() : null;
        }
        return null;
    }
    
    private String extractTokenFromRequest(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }
}
