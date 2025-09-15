package com.javabattle.arena.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;

@Service
public class ProblemService {
    
    private final String CLAUDE_API_URL = "https://api.anthropic.com/v1/messages";
    @Value("${claude.api.key}") private String apiKey;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpClient httpClient = HttpClient.newHttpClient();
    
    public Map<String, Object> generateRandomProblem() {
        try {
            return generateAIProblem();
        } catch (Exception e) {
            System.err.println("AI 문제 생성 실패, 기본 문제 사용: " + e.getMessage());
            return generateFallbackProblem();
        }
    }
    
    private Map<String, Object> generateAIProblem() throws IOException, InterruptedException {
        String prompt = "Java 프로그래밍 초급자를 위한 코딩 문제를 하나 생성해주세요.\n\n" +
            "다음 형식으로 정확히 응답해주세요:\n\n" +
            "TITLE: [문제 제목]\n" +
            "DESCRIPTION: [문제 설명 - 입력/출력 형식 포함]\n" +
            "TESTCASE1_INPUT: [테스트케이스1 입력]\n" +
            "TESTCASE1_OUTPUT: [테스트케이스1 출력]\n" +
            "TESTCASE2_INPUT: [테스트케이스2 입력]\n" +
            "TESTCASE2_OUTPUT: [테스트케이스2 출력]\n" +
            "TESTCASE3_INPUT: [테스트케이스3 입력]\n" +
            "TESTCASE3_OUTPUT: [테스트케이스3 출력]\n\n" +
            "요구사항:\n" +
            "- Scanner로 입력받는 간단한 Java 문제\n" +
            "- 초급자 수준 (if, for, while, 배열, 문자열 처리)\n" +
            "- 명확한 입력/출력 형식\n" +
            "- 실행 가능한 테스트케이스 3개";
            
        String requestBody = "{\n" +
            "\"model\": \"claude-3-5-sonnet-20241022\",\n" +
            "\"max_tokens\": 1000,\n" +
            "\"messages\": [\n" +
            "{\n" +
            "\"role\": \"user\",\n" +
            "\"content\": \"" + prompt.replace("\"", "\\\"").replace("\n", "\\n") + "\"\n" +
            "}\n" +
            "]\n" +
            "}";
            
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(CLAUDE_API_URL))
                .header("Content-Type", "application/json")
                .header("x-api-key", apiKey)
                .header("anthropic-version", "2023-06-01")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();
                
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        
        if (response.statusCode() != 200) {
            throw new IOException("API 호출 실패: " + response.statusCode() + " - " + response.body());
        }
        
        JsonNode jsonResponse = objectMapper.readTree(response.body());
        String content = jsonResponse.get("content").get(0).get("text").asText();
        
        return parseAIResponse(content);
    }
    
    private Map<String, Object> parseAIResponse(String content) {
        Map<String, Object> problem = new HashMap<>();
        List<Map<String, Object>> testCases = new ArrayList<>();
        
        String[] lines = content.split("\n");
        String title = "";
        StringBuilder description = new StringBuilder();
        String currentTestInput = "";
        String currentTestOutput = "";
        int testCaseCount = 0;
        
        for (String line : lines) {
            line = line.trim();
            
            if (line.startsWith("TITLE:")) {
                title = line.substring(6).trim();
            } else if (line.startsWith("DESCRIPTION:")) {
                description.append(line.substring(12).trim());
            } else if (line.startsWith("TESTCASE") && line.contains("_INPUT:")) {
                currentTestInput = line.substring(line.indexOf(":") + 1).trim();
            } else if (line.startsWith("TESTCASE") && line.contains("_OUTPUT:")) {
                currentTestOutput = line.substring(line.indexOf(":") + 1).trim();
                
                if (!currentTestInput.isEmpty() && !currentTestOutput.isEmpty()) {
                    Map<String, Object> testCase = new HashMap<>();
                    testCase.put("input", currentTestInput);
                    testCase.put("output", currentTestOutput);
                    testCases.add(testCase);
                    testCaseCount++;
                }
                
                currentTestInput = "";
                currentTestOutput = "";
            } else if (!line.isEmpty() && !line.startsWith("TITLE:") && !line.startsWith("DESCRIPTION:") && !line.startsWith("TESTCASE")) {
                if (description.length() > 0) {
                    description.append("\n");
                }
                description.append(line);
            }
        }
        
        problem.put("title", title.isEmpty() ? "AI 생성 문제" : title);
        problem.put("description", description.toString().isEmpty() ? "AI가 생성한 프로그래밍 문제입니다." : description.toString());
        problem.put("testCases", testCases.isEmpty() ? generateDefaultTestCases() : testCases);
        
        return problem;
    }
    
    private List<Map<String, Object>> generateDefaultTestCases() {
        List<Map<String, Object>> testCases = new ArrayList<>();
        Map<String, Object> testCase = new HashMap<>();
        testCase.put("input", "5 3");
        testCase.put("output", "8");
        testCases.add(testCase);
        return testCases;
    }
    
    private Map<String, Object> generateFallbackProblem() {
        Random random = new Random();
        int a = random.nextInt(50) + 1;
        int b = random.nextInt(50) + 1;
        
        Map<String, Object> problem = new HashMap<>();
        problem.put("title", "두 수의 합");
        problem.put("description", String.format(
            "두 정수 A와 B를 입력받은 다음, A+B를 출력하는 프로그램을 작성하시오.\n\n" +
            "입력:\n첫째 줄에 A와 B가 주어진다.\n\n" +
            "출력:\n첫째 줄에 A+B를 출력한다.\n\n" +
            "예제 입력:\n%d %d\n\n예제 출력:\n%d", a, b, a + b
        ));
        
        List<Map<String, Object>> testCases = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            int x = random.nextInt(100) + 1;
            int y = random.nextInt(100) + 1;
            Map<String, Object> testCase = new HashMap<>();
            testCase.put("input", x + " " + y);
            testCase.put("output", String.valueOf(x + y));
            testCases.add(testCase);
        }
        problem.put("testCases", testCases);
        
        return problem;
    }
    
    public boolean validateSolution(String code, Map<String, Object> problem) {
        try {
            List<Map<String, Object>> testCases = (List<Map<String, Object>>) problem.get("testCases");
            
            for (Map<String, Object> testCase : testCases) {
                String input = (String) testCase.get("input");
                String expectedOutput = (String) testCase.get("output");
                
                String actualOutput = executeCode(code, input);
                if (!expectedOutput.trim().equals(actualOutput.trim())) {
                    return false;
                }
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    private String executeCode(String code, String input) {
        return "3";
    }
}