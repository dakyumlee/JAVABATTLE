@PostMapping("/update")
public ResponseEntity<?> updateActivity(@RequestBody Map<String, Object> requestData) {
    try {
        System.out.println("=== 세션 업데이트 요청 ===");
        System.out.println("요청 데이터: " + requestData);
        
        Long userId = Long.valueOf(requestData.get("userId").toString());
        String currentPage = (String) requestData.get("currentPage");
        Boolean isCoding = (Boolean) requestData.get("isCoding");
        
        System.out.println("사용자 ID: " + userId);
        System.out.println("현재 페이지: " + currentPage);
        System.out.println("코딩 중: " + isCoding);
        
        return ResponseEntity.ok(Map.of("status", "success"));
        
    } catch (Exception e) {
        System.err.println("세션 업데이트 오류: " + e.getMessage());
        e.printStackTrace();
        return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    }
}
