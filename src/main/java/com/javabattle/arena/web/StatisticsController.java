package com.javabattle.arena.web;

import com.javabattle.arena.model.User;
import com.javabattle.arena.repository.UserRepository;
import com.javabattle.arena.repository.StudentActivityRepository;
import com.javabattle.arena.repository.MaterialAccessRepository;
import com.javabattle.arena.repository.ProblemSubmissionRepository;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@CrossOrigin(origins = "*")
public class StatisticsController {

    private final UserRepository userRepository;
    private final StudentActivityRepository studentActivityRepository;
    private final MaterialAccessRepository materialAccessRepository;
    private final ProblemSubmissionRepository problemSubmissionRepository;

    public StatisticsController(UserRepository userRepository,
                               StudentActivityRepository studentActivityRepository,
                               MaterialAccessRepository materialAccessRepository,
                               ProblemSubmissionRepository problemSubmissionRepository) {
        this.userRepository = userRepository;
        this.studentActivityRepository = studentActivityRepository;
        this.materialAccessRepository = materialAccessRepository;
        this.problemSubmissionRepository = problemSubmissionRepository;
    }

    @GetMapping("/statistics")
    public String statisticsPage() {
        return "statistics";
    }

    @GetMapping("/api/statistics/overview")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getOverview() {
        try {
            LocalDateTime today = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
            LocalDateTime weekAgo = LocalDateTime.now().minusDays(7);
            
            Long todayActiveStudents = 0L;
            Long currentlyActive = 0L;
            Long weeklySubmissions = 0L;
            Long totalStudents = 0L;
            
            try {
                todayActiveStudents = studentActivityRepository.countActiveStudentsSince(today);
            } catch (Exception e) {}
            
            try {
                currentlyActive = studentActivityRepository.countActiveStudentsSince(LocalDateTime.now().minusMinutes(5));
            } catch (Exception e) {}
            
            try {
                weeklySubmissions = problemSubmissionRepository.countBySubmittedAtAfter(weekAgo);
            } catch (Exception e) {}
            
            try {
                totalStudents = userRepository.countByRole("STUDENT");
            } catch (Exception e) {}
            
            Map<String, Object> overview = new HashMap<>();
            overview.put("todayActiveStudents", todayActiveStudents != null ? todayActiveStudents : 0);
            overview.put("currentlyActive", currentlyActive != null ? currentlyActive : 0);
            overview.put("weeklySubmissions", weeklySubmissions != null ? weeklySubmissions : 0);
            overview.put("totalStudents", totalStudents != null ? totalStudents : 0);
            
            return ResponseEntity.ok(overview);
        } catch (Exception e) {
            Map<String, Object> overview = new HashMap<>();
            overview.put("todayActiveStudents", 0);
            overview.put("currentlyActive", 0);
            overview.put("weeklySubmissions", 0);
            overview.put("totalStudents", 0);
            return ResponseEntity.ok(overview);
        }
    }

    @GetMapping("/api/statistics/activity-chart")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getActivityChart(@RequestParam(defaultValue = "7") int days) {
        try {
            LocalDateTime since = LocalDateTime.now().minusDays(days);
            List<Object[]> activityStats = null;
            
            try {
                activityStats = studentActivityRepository.getActivityStatsSince(since);
            } catch (Exception e) {}
            
            List<String> labels = new ArrayList<>();
            List<Integer> data = new ArrayList<>();
            
            if (activityStats != null) {
                for (Object[] stat : activityStats) {
                    labels.add((String) stat[0]);
                    data.add(((Number) stat[1]).intValue());
                }
            }
            
            Map<String, Object> result = new HashMap<>();
            result.put("labels", labels);
            result.put("data", data);
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> result = new HashMap<>();
            result.put("labels", new ArrayList<>());
            result.put("data", new ArrayList<>());
            return ResponseEntity.ok(result);
        }
    }

    @GetMapping("/api/statistics/daily-stats")
    @ResponseBody
    public ResponseEntity<List<Map<String, Object>>> getDailyStats(@RequestParam(defaultValue = "7") int days) {
        try {
            List<Map<String, Object>> dailyStats = new ArrayList<>();
            
            for (int i = days - 1; i >= 0; i--) {
                LocalDateTime date = LocalDateTime.now().minusDays(i);
                LocalDateTime dayStart = date.withHour(0).withMinute(0).withSecond(0);
                
                Long activeStudents = 0L;
                try {
                    activeStudents = studentActivityRepository.countActiveStudentsSince(dayStart);
                } catch (Exception e) {}
                
                Map<String, Object> dayStat = new HashMap<>();
                dayStat.put("date", date.format(DateTimeFormatter.ofPattern("MM-dd")));
                dayStat.put("activeStudents", activeStudents != null ? activeStudents : 0);
                dailyStats.add(dayStat);
            }
            
            return ResponseEntity.ok(dailyStats);
        } catch (Exception e) {
            return ResponseEntity.ok(new ArrayList<>());
        }
    }

    @GetMapping("/api/statistics/popular-materials")
    @ResponseBody
    public ResponseEntity<List<Map<String, Object>>> getPopularMaterials(@RequestParam(defaultValue = "7") int days) {
        try {
            LocalDateTime since = LocalDateTime.now().minusDays(days);
            List<Object[]> popularMaterials = null;
            
            try {
                popularMaterials = materialAccessRepository.getPopularMaterialsSince(since);
            } catch (Exception e) {}
            
            List<Map<String, Object>> result = new ArrayList<>();
            
            if (popularMaterials != null) {
                result = popularMaterials.stream()
                        .map(material -> {
                            Map<String, Object> item = new HashMap<>();
                            item.put("name", material[0]);
                            item.put("accessCount", material[1]);
                            return item;
                        })
                        .collect(Collectors.toList());
            }
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.ok(new ArrayList<>());
        }
    }

    @GetMapping("/api/statistics/student-performance")
    @ResponseBody
    public ResponseEntity<List<Map<String, Object>>> getStudentPerformance() {
        try {
            List<User> students = null;
            try {
                students = userRepository.findByRole("STUDENT");
            } catch (Exception e) {}
            
            List<Map<String, Object>> performance = new ArrayList<>();
            
            if (students != null) {
                for (User student : students) {
                    Long problemsSolved = 0L;
                    Double averageScore = 0.0;
                    LocalDateTime lastActivity = LocalDateTime.now();
                    
                    try {
                        problemsSolved = problemSubmissionRepository.countByUserIdAndScoreIsNotNull(student.getId());
                    } catch (Exception e) {}
                    
                    try {
                        averageScore = problemSubmissionRepository.getAverageScore();
                    } catch (Exception e) {}
                    
                    try {
                        lastActivity = studentActivityRepository.findTopByStudentIdOrderByTimestampDesc(student.getId());
                    } catch (Exception e) {}
                    
                    Map<String, Object> studentPerf = new HashMap<>();
                    studentPerf.put("nickname", student.getNickname());
                    studentPerf.put("email", student.getEmail());
                    studentPerf.put("problemsSolved", problemsSolved != null ? problemsSolved : 0);
                    studentPerf.put("quizzesCompleted", 0);
                    studentPerf.put("averageScore", averageScore != null && averageScore > 0 ? averageScore.intValue() : 0);
                    studentPerf.put("lastActivity", lastActivity != null ? lastActivity : LocalDateTime.now());
                    
                    performance.add(studentPerf);
                }
            }
            
            return ResponseEntity.ok(performance);
        } catch (Exception e) {
            return ResponseEntity.ok(new ArrayList<>());
        }
    }

    @GetMapping("/api/statistics/export/csv")
    @ResponseBody
    public ResponseEntity<byte[]> exportCSV() {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            OutputStreamWriter writer = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);
            
            outputStream.write(0xEF);
            outputStream.write(0xBB);
            outputStream.write(0xBF);
            
            writer.write("학생명,이메일,문제해결수,평균점수\n");
            
            List<User> students = null;
            try {
                students = userRepository.findByRole("STUDENT");
            } catch (Exception e) {}
            
            if (students != null && !students.isEmpty()) {
                for (User student : students) {
                    Long problemsSolved = 0L;
                    Double averageScore = 0.0;
                    
                    try {
                        problemsSolved = problemSubmissionRepository.countByUserIdAndScoreIsNotNull(student.getId());
                    } catch (Exception e) {}
                    
                    try {
                        averageScore = problemSubmissionRepository.getAverageScore();
                    } catch (Exception e) {}
                    
                    writer.write(String.format("%s,%s,%d,%d\n",
                        student.getNickname() != null ? student.getNickname() : "학생",
                        student.getEmail() != null ? student.getEmail() : "email@test.com",
                        problemsSolved != null ? problemsSolved : 0,
                        averageScore != null && averageScore > 0 ? averageScore.intValue() : 0
                    ));
                }
            } else {
                writer.write("데이터 없음,-,-,-\n");
            }
            
            writer.flush();
            writer.close();
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("text/csv"));
            headers.setContentDispositionFormData("attachment", 
                "학습통계_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + ".csv");
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(outputStream.toByteArray());
                    
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/api/statistics/export/pdf")
    @ResponseBody
    public ResponseEntity<byte[]> exportPDF() {
        try {
            StringBuilder html = new StringBuilder();
            html.append("<!DOCTYPE html><html><head>");
            html.append("<meta charset='UTF-8'>");
            html.append("<title>학습 통계 리포트</title>");
            html.append("<style>body{font-family:Arial,sans-serif;margin:20px;}table{border-collapse:collapse;width:100%;}th,td{border:1px solid #ddd;padding:8px;text-align:left;}th{background-color:#f2f2f2;}</style>");
            html.append("</head><body>");
            html.append("<h1>학습 통계 리포트</h1>");
            html.append("<p>생성일: ").append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))).append("</p>");
            html.append("<table><tr><th>학생명</th><th>이메일</th><th>문제해결수</th><th>평균점수</th></tr>");
            
            List<User> students = null;
            try {
                students = userRepository.findByRole("STUDENT");
            } catch (Exception e) {}
            
            if (students != null && !students.isEmpty()) {
                for (User student : students) {
                    Long problemsSolved = 0L;
                    Double averageScore = 0.0;
                    
                    try {
                        problemsSolved = problemSubmissionRepository.countByUserIdAndScoreIsNotNull(student.getId());
                    } catch (Exception e) {}
                    
                    try {
                        averageScore = problemSubmissionRepository.getAverageScore();
                    } catch (Exception e) {}
                    
                    html.append("<tr><td>").append(student.getNickname() != null ? student.getNickname() : "학생")
                        .append("</td><td>").append(student.getEmail() != null ? student.getEmail() : "email@test.com")
                        .append("</td><td>").append(problemsSolved != null ? problemsSolved : 0)
                        .append("</td><td>").append(averageScore != null && averageScore > 0 ? averageScore.intValue() : 0)
                        .append("</td></tr>");
                }
            } else {
                html.append("<tr><td>데이터 없음</td><td>-</td><td>-</td><td>-</td></tr>");
            }
            
            html.append("</table></body></html>");
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.TEXT_HTML);
            headers.setContentDispositionFormData("attachment", 
                "학습통계_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + ".html");
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(html.toString().getBytes(StandardCharsets.UTF_8));
                    
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }
}