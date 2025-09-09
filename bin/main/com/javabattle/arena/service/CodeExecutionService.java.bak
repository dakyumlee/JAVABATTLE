package com.javabattle.arena.service;

import org.springframework.stereotype.Service;
import java.io.*;
import java.nio.file.*;
import java.util.concurrent.TimeUnit;

@Service
public class CodeExecutionService {
    
    private final String TEMP_DIR = System.getProperty("java.io.tmpdir") + "/javabattle/";
    
    public CodeExecutionResult executeJavaCode(String code, String input) {
        try {
            Files.createDirectories(Paths.get(TEMP_DIR));
            
            String className = extractClassName(code);
            if (className == null) {
                return new CodeExecutionResult(false, "", "클래스명을 찾을 수 없습니다.");
            }
            
            String javaFilePath = TEMP_DIR + className + ".java";
            String classFilePath = TEMP_DIR + className + ".class";
            
            Files.write(Paths.get(javaFilePath), code.getBytes());
            
            ProcessBuilder compileProcess = new ProcessBuilder("javac", javaFilePath);
            compileProcess.directory(new File(TEMP_DIR));
            Process compile = compileProcess.start();
            
            if (!compile.waitFor(5, TimeUnit.SECONDS)) {
                compile.destroyForcibly();
                return new CodeExecutionResult(false, "", "컴파일 시간 초과");
            }
            
            if (compile.exitValue() != 0) {
                String error = readStream(compile.getErrorStream());
                return new CodeExecutionResult(false, "", "컴파일 오류: " + error);
            }
            
            ProcessBuilder runProcess = new ProcessBuilder("java", "-cp", TEMP_DIR, className);
            runProcess.directory(new File(TEMP_DIR));
            Process run = runProcess.start();
            
            if (input != null && !input.isEmpty()) {
                try (OutputStreamWriter writer = new OutputStreamWriter(run.getOutputStream())) {
                    writer.write(input);
                    writer.flush();
                }
            }
            
            if (!run.waitFor(5, TimeUnit.SECONDS)) {
                run.destroyForcibly();
                return new CodeExecutionResult(false, "", "실행 시간 초과");
            }
            
            String output = readStream(run.getInputStream());
            String error = readStream(run.getErrorStream());
            
            Files.deleteIfExists(Paths.get(javaFilePath));
            Files.deleteIfExists(Paths.get(classFilePath));
            
            if (run.exitValue() != 0) {
                return new CodeExecutionResult(false, output, "실행 오류: " + error);
            }
            
            return new CodeExecutionResult(true, output.trim(), "");
            
        } catch (Exception e) {
            return new CodeExecutionResult(false, "", "시스템 오류: " + e.getMessage());
        }
    }
    
    private String extractClassName(String code) {
        String[] lines = code.split("\n");
        for (String line : lines) {
            line = line.trim();
            if (line.startsWith("public class ")) {
                String[] parts = line.split(" ");
                for (int i = 0; i < parts.length - 1; i++) {
                    if ("class".equals(parts[i])) {
                        return parts[i + 1].replaceAll("[^a-zA-Z0-9]", "");
                    }
                }
            }
        }
        return null;
    }
    
    private String readStream(InputStream stream) throws IOException {
        StringBuilder result = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line).append("\n");
            }
        }
        return result.toString();
    }
    
    public static class CodeExecutionResult {
        private final boolean success;
        private final String output;
        private final String error;
        
        public CodeExecutionResult(boolean success, String output, String error) {
            this.success = success;
            this.output = output;
            this.error = error;
        }
        
        public boolean isSuccess() { return success; }
        public String getOutput() { return output; }
        public String getError() { return error; }
    }
}