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
import java.util.stream.Collectors;

@Controller
@RequestMapping("/api/teacher/statistics")
public class StatisticsController {

    @Autowired
    private StudentActivityRepository studentActivityRepository;
    
    @Autowired
    private MaterialAccessRepository materialAccessRepository;
    
    @Autowired
    private LearningStatisticsRepository learningStatisticsRepository;
    
    @Autowired
    private ProblemSubmissionRepository problemSubmissionRepository;
    
    @Autowired
    private QuizSubmissionRepository quizSubmissionRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping("/statistics")
    public String statisticsPage(HttpServletRequest request, Model model) {
        String token = extractToken(request);
        if (token == null) {
            return "redirect:/";
        }
        
        JwtUtil.UserInfo userInfo = jwtUtil.validateToken(token);
        if (userInfo == null || (!userInfo.getRole().equals("TEACHER") && !userInfo.getRole().equals("ADMIN"))) {
            return "redirect:/";
        }
        
        model.addAttribute("user", userInfo);
        return "statistics";
    }

    @GetMapping("/api/statistics/overview")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getOverview(HttpServletRequest request) {
        if (!isAuthorized(request)) {
            return ResponseEntity.status(403).build();
        }
        
        Map<String, Object> overview = new HashMap<>();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime todayStart = now.toLocalDate().atStartOfDay();
        LocalDateTime weekStart = now.minusDays(7);
        
        Long todayActiveStudents = studentActivityRepository.countActiveStudentsSince(todayStart);
        overview.put("todayActiveStudents", todayActiveStudents);
        
        Long currentlyActive = studentActivityRepository.countActiveStudentsSince(now.minusMinutes(30));
        overview.put("currentlyActive", currentlyActive);
        
        Long weeklySubmissions = problemSubmissionRepository.countBySubmittedAtAfter(weekStart);
        overview.put("weeklySubmissions", weeklySubmissions);
        
        Long weeklyMaterialAccess = materialAccessRepository.countByAccessTimeAfter(weekStart);
        overview.put("weeklyMaterialAccess", weeklyMaterialAccess);
        
        Long totalStudents = userRepository.countByRole("STUDENT");
        overview.put("totalStudents", totalStudents);
        
        return ResponseEntity.ok(overview);
    }

    @GetMapping("/activity-chart")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getActivityChart(
            @RequestParam(defaultValue = "7") int days,
            HttpServletRequest request) {
        
        if (!isAuthorized(request)) {
            return ResponseEntity.status(403).build();
        }
        
        LocalDateTime start = LocalDateTime.now().minusDays(days);
        List<Object[]> activityStats = studentActivityRepository.getActivityStatsSince(start);
        
        Map<String, Object> chartData = new HashMap<>();
        List<String> labels = new ArrayList<>();
        List<Long> data = new ArrayList<>();
        
        for (Object[] stat : activityStats) {
            labels.add((String) stat[0]);
            data.add((Long) stat[1]);
        }
        
        chartData.put("labels", labels);
        chartData.put("data", data);
        
        return ResponseEntity.ok(chartData);
    }

    @GetMapping("/popular-materials")
    @ResponseBody
    public ResponseEntity<List<Map<String, Object>>> getPopularMaterials(
            @RequestParam(defaultValue = "7") int days,
            HttpServletRequest request) {
        
        if (!isAuthorized(request)) {
            return ResponseEntity.status(403).build();
        }
        
        LocalDateTime start = LocalDateTime.now().minusDays(days);
        List<Object[]> popularMaterials = materialAccessRepository.getPopularMaterialsSince(start);
        
        List<Map<String, Object>> result = new ArrayList<>();
        for (Object[] material : popularMaterials) {
            Map<String, Object> item = new HashMap<>();
            item.put("name", material[0]);
            item.put("accessCount", material[1]);
            result.add(item);
        }
        
        return ResponseEntity.ok(result);
    }

    @GetMapping("/student-performance")
    @ResponseBody
    public ResponseEntity<List<Map<String, Object>>> getStudentPerformance(HttpServletRequest request) {
        if (!isAuthorized(request)) {
            return ResponseEntity.status(403).build();
        }
        
        List<User> students = userRepository.findByRole("STUDENT");
        List<Map<String, Object>> performance = new ArrayList<>();
        
        for (User student : students) {
            Map<String, Object> stats = new HashMap<>();
            stats.put("nickname", student.getNickname());
            stats.put("email", student.getEmail());
            
            Long problemsSolved = problemSubmissionRepository.countByUserIdAndScoreIsNotNull(student.getId());
            stats.put("problemsSolved", problemsSolved);
            
            Long quizzesCompleted = quizSubmissionRepository.countByUserId(student.getId());
            stats.put("quizzesCompleted", quizzesCompleted);
            
            Double avgScore = quizSubmissionRepository.getAverageScoreByUserId(student.getId());
            stats.put("averageScore", avgScore != null ? Math.round(avgScore * 100.0) / 100.0 : 0.0);
            
            LocalDateTime lastActivity = studentActivityRepository.findTopByStudentIdOrderByTimestampDesc(student.getId());
            stats.put("lastActivity", lastActivity);
            
            performance.add(stats);
        }
        
        return ResponseEntity.ok(performance);
    }

    @GetMapping("/daily-stats")
    @ResponseBody
    public ResponseEntity<List<Map<String, Object>>> getDailyStats(
            @RequestParam(defaultValue = "30") int days,
            HttpServletRequest request) {
        
        if (!isAuthorized(request)) {
            return ResponseEntity.status(403).build();
        }
        
        List<Map<String, Object>> dailyStats = new ArrayList<>();
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(days);
        
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            LocalDateTime dayStart = date.atStartOfDay();
            LocalDateTime dayEnd = dayStart.plusDays(1);
            
            Map<String, Object> dayStats = new HashMap<>();
            dayStats.put("date", date.toString());
            
            Long activeStudents = studentActivityRepository.countActiveStudentsSince(dayStart);
            dayStats.put("activeStudents", activeStudents);
            
            Long submissions = problemSubmissionRepository.countBySubmittedAtBetween(dayStart, dayEnd);
            dayStats.put("submissions", submissions);
            
            Long materialAccess = materialAccessRepository.countByAccessTimeBetween(dayStart, dayEnd);
            dayStats.put("materialAccess", materialAccess);
            
            dailyStats.add(dayStats);
        }
        
        return ResponseEntity.ok(dailyStats);
    }

    @GetMapping("/export/csv")
    public ResponseEntity<String> exportCSV(HttpServletRequest request) {
        if (!isAuthorized(request)) {
            return ResponseEntity.status(403).build();
        }
        
        try {
            StringBuilder csv = new StringBuilder();
            csv.append("학생명,이메일,문제해결수,퀴즈완료수,평균점수,최근활동\n");
            
            List<User> students = userRepository.findByRole("STUDENT");
            for (User student : students) {
                Long problemsSolved = problemSubmissionRepository.countByUserIdAndScoreIsNotNull(student.getId());
                Long quizzesCompleted = quizSubmissionRepository.countByUserId(student.getId());
                Double avgScore = quizSubmissionRepository.getAverageScoreByUserId(student.getId());
                LocalDateTime lastActivity = studentActivityRepository.findTopByStudentIdOrderByTimestampDesc(student.getId());
                
                csv.append(String.format("%s,%s,%d,%d,%.2f,%s\n",
                    student.getNickname(),
                    student.getEmail(),
                    problemsSolved,
                    quizzesCompleted,
                    avgScore != null ? avgScore : 0.0,
                    lastActivity != null ? lastActivity.toString() : "활동없음"
                ));
            }
            
            return ResponseEntity.ok()
                .header("Content-Type", "text/csv; charset=UTF-8")
                .header("Content-Disposition", "attachment; filename=learning_statistics.csv")
                .body(csv.toString());
                
        } catch (Exception e) {
            return ResponseEntity.status(500).body("CSV 생성 중 오류 발생");
        }
    }

    @GetMapping("/export/pdf")
    public ResponseEntity<byte[]> exportPDF(HttpServletRequest request) {
        if (!isAuthorized(request)) {
            return ResponseEntity.status(403).build();
        }
        
        try {
            StringBuilder html = new StringBuilder();
            html.append("<!DOCTYPE html><html><head><meta charset='UTF-8'>");
            html.append("<style>");
            html.append("body { font-family: 'Malgun Gothic', sans-serif; }");
            html.append("table { width: 100%; border-collapse: collapse; }");
            html.append("th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }");
            html.append("th { background-color: #f2f2f2; }");
            html.append("h1 { color: #333; }");
            html.append("</style>");
            html.append("</head><body>");
            html.append("<h1>Java Battle Arena 학습 통계 리포트</h1>");
            html.append("<p>생성일시: " + LocalDateTime.now().toString() + "</p>");
            html.append("<table>");
            html.append("<tr><th>학생명</th><th>이메일</th><th>문제해결수</th><th>퀴즈완료수</th><th>평균점수</th><th>최근활동</th></tr>");
            
            List<User> students = userRepository.findByRole("STUDENT");
            for (User student : students) {
                Long problemsSolved = problemSubmissionRepository.countByUserIdAndScoreIsNotNull(student.getId());
                Long quizzesCompleted = quizSubmissionRepository.countByUserId(student.getId());
                Double avgScore = quizSubmissionRepository.getAverageScoreByUserId(student.getId());
                LocalDateTime lastActivity = studentActivityRepository.findTopByStudentIdOrderByTimestampDesc(student.getId());
                
                html.append("<tr>");
                html.append("<td>" + student.getNickname() + "</td>");
                html.append("<td>" + student.getEmail() + "</td>");
                html.append("<td>" + problemsSolved + "</td>");
                html.append("<td>" + quizzesCompleted + "</td>");
                html.append("<td>" + String.format("%.2f", avgScore != null ? avgScore : 0.0) + "</td>");
                html.append("<td>" + (lastActivity != null ? lastActivity.toString() : "활동없음") + "</td>");
                html.append("</tr>");
            }
            
            html.append("</table>");
            html.append("</body></html>");
            
            byte[] pdfBytes = html.toString().getBytes("UTF-8");
            
            return ResponseEntity.ok()
                .header("Content-Type", "application/pdf")
                .header("Content-Disposition", "attachment; filename=learning_statistics.pdf")
                .body(pdfBytes);
                
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    private boolean isAuthorized(HttpServletRequest request) {
        String token = extractToken(request);
        if (token == null) return false;
        
        JwtUtil.UserInfo userInfo = jwtUtil.validateToken(token);
        return userInfo != null && (userInfo.getRole().equals("TEACHER") || userInfo.getRole().equals("ADMIN"));
    }

    private String extractToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }
}