@GetMapping("/api/teacher/active-students")
public ResponseEntity<List<Map<String, Object>>> getActiveStudents() {
    try {
        List<ActiveSession> sessions = sessionService.getActiveSessions();
        
        // 시간 필터링 제거하여 모든 IS_ACTIVE=true 세션을 표시
        List<Map<String, Object>> result = sessions.stream().map(session -> {
            Map<String, Object> studentData = new HashMap<>();
            studentData.put("userId", session.getUserId());
            studentData.put("sessionId", session.getSessionId());
            studentData.put("currentPage", session.getCurrentPage());
            studentData.put("lastActivity", session.getLastActivity());
            studentData.put("isCoding", session.getIsCoding());
            studentData.put("codeLength", session.getCurrentCode() != null ? session.getCurrentCode().length() : 0);
            studentData.put("startTime", session.getStartTime());
            // 모든 세션을 활성으로 표시
            studentData.put("isActive", true);
            return studentData;
        }).collect(Collectors.toList());
        
        return ResponseEntity.ok(result);
    } catch (Exception e) {
        e.printStackTrace();
        return ResponseEntity.badRequest().build();
    }
}
