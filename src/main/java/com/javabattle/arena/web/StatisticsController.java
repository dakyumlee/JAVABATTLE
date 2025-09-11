package com.javabattle.arena.web;

import com.javabattle.arena.config.JwtUtil;
import com.javabattle.arena.repository.*;
import com.javabattle.arena.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.*;

@Controller
public class StatisticsController {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @GetMapping("/statistics")
    public String statisticsPage() {
        return "statistics";
    }
    
    @GetMapping("/api/statistics/overview")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getOverview() {
        Map<String, Object> overview = new HashMap<>();
        
        // 임시 데이터
        overview.put("todayActiveStudents", 5L);
        overview.put("currentlyActive", 2L);
        overview.put("weeklySubmissions", 23L);
        overview.put("weeklyMaterialAccess", 45L);
        overview.put("totalStudents", 12L);
        
        return ResponseEntity.ok(overview);
    }
}
