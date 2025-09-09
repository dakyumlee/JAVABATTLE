package com.javabattle.arena.web;

import com.javabattle.arena.model.LearningStatistics;
import com.javabattle.arena.model.User;
import com.javabattle.arena.repository.LearningStatisticsRepository;
import com.javabattle.arena.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/statistics")
@CrossOrigin(origins = "*")
public class StatisticsController {
    
    @Autowired
    private LearningStatisticsRepository statisticsRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @GetMapping("/overview")
    public Map<String, Object> getOverview() {
        Map<String, Object> overview = new HashMap<>();
        
        LocalDateTime weekAgo = LocalDateTime.now().minusDays(7);
        
        overview.put("totalUsers", userRepository.count());
        overview.put("activeUsers", statisticsRepository.getActiveUsersCount(weekAgo));
        overview.put("averageProblems", statisticsRepository.getAverageProblemsSolved());
        overview.put("averageStudyTime", statisticsRepository.getAverageStudyTime());
        
        return overview;
    }
    
    @GetMapping("/leaderboard")
    public List<Map<String, Object>> getLeaderboard() {
        List<LearningStatistics> topPerformers = statisticsRepository.findTopPerformers();
        
        return topPerformers.stream().limit(10).map(stats -> {
            User user = userRepository.findById(stats.getUserId()).orElse(null);
            Map<String, Object> entry = new HashMap<>();
            entry.put("userId", stats.getUserId());
            entry.put("nickname", user != null ? user.getNickname() : "Unknown");
            entry.put("problemsSolved", stats.getProblemsSolved());
            entry.put("studyTime", stats.getTotalStudyTime());
            entry.put("accuracy", stats.getProblemsAttempted() > 0 ? 
                (double) stats.getProblemsSolved() / stats.getProblemsAttempted() * 100 : 0);
            return entry;
        }).collect(Collectors.toList());
    }
    
    @GetMapping("/user/{userId}")
    public Map<String, Object> getUserStatistics(@PathVariable Long userId) {
        LearningStatistics stats = statisticsRepository.findByUserId(userId)
            .orElse(new LearningStatistics(userId));
        
        Map<String, Object> userStats = new HashMap<>();
        userStats.put("totalStudyTime", stats.getTotalStudyTime());
        userStats.put("problemsSolved", stats.getProblemsSolved());
        userStats.put("problemsAttempted", stats.getProblemsAttempted());
        userStats.put("quizCorrect", stats.getQuizCorrect());
        userStats.put("quizTotal", stats.getQuizTotal());
        userStats.put("streakDays", stats.getStreakDays());
        userStats.put("accuracy", stats.getProblemsAttempted() > 0 ? 
            (double) stats.getProblemsSolved() / stats.getProblemsAttempted() * 100 : 0);
        userStats.put("quizAccuracy", stats.getQuizTotal() > 0 ? 
            (double) stats.getQuizCorrect() / stats.getQuizTotal() * 100 : 0);
        
        return userStats;
    }
    
    @PostMapping("/update/{userId}")
    public Map<String, Object> updateStatistics(@PathVariable Long userId, @RequestBody Map<String, Object> data) {
        LearningStatistics stats = statisticsRepository.findByUserId(userId)
            .orElse(new LearningStatistics(userId));
        
        if (data.get("studyTime") != null) {
            stats.setTotalStudyTime(stats.getTotalStudyTime() + (Integer) data.get("studyTime"));
        }
        
        if (data.get("problemSolved") != null && (Boolean) data.get("problemSolved")) {
            stats.setProblemsSolved(stats.getProblemsSolved() + 1);
        }
        
        if (data.get("problemAttempted") != null && (Boolean) data.get("problemAttempted")) {
            stats.setProblemsAttempted(stats.getProblemsAttempted() + 1);
        }
        
        if (data.get("quizCorrect") != null && (Boolean) data.get("quizCorrect")) {
            stats.setQuizCorrect(stats.getQuizCorrect() + 1);
        }
        
        if (data.get("quizAttempted") != null && (Boolean) data.get("quizAttempted")) {
            stats.setQuizTotal(stats.getQuizTotal() + 1);
        }
        
        stats.setLastActivity(LocalDateTime.now());
        stats.setUpdatedAt(LocalDateTime.now());
        
        statisticsRepository.save(stats);
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        return result;
    }
}
