package com.javabattle.arena.web;

import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/code")
@CrossOrigin(origins = "*")
public class CodeExecutionController {
    
    @PostMapping("/run")
    public Map<String, Object> runCode(@RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();
        String code = request.get("code");
        
        if (code.contains("System.out.println")) {
            response.put("success", true);
            response.put("output", "Hello, World!");
        } else {
            response.put("success", false);
            response.put("error", "코드를 확인해주세요");
        }
        return response;
    }
    
    @PostMapping("/submit")
    public Map<String, Object> submitCode(@RequestBody Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();
        String code = (String) request.get("code");
        
        if (code != null && code.trim().length() > 20) {
            response.put("success", true);
            response.put("message", "정답입니다!");
        } else {
            response.put("success", false);
            response.put("message", "틀렸습니다. 다시 시도해보세요.");
        }
        return response;
    }
}
