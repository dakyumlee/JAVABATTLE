// SessionService.java에서 updateActivity 메서드 수정
@Transactional
public void updateActivity(Map<String, Object> activityData) {
    try {
        Long userId = Long.valueOf(activityData.get("userId").toString());
        String page = (String) activityData.get("page");
        String code = (String) activityData.get("code");
        Boolean isCoding = (Boolean) activityData.get("isCoding");
        
        // CLOB 문제 때문에 코드 저장은 생략하고 다른 필드만 업데이트
        Optional<ActiveSession> sessionOpt = activeSessionRepository.findByUserIdAndIsActiveTrue(userId);
        
        if (sessionOpt.isPresent()) {
            ActiveSession session = sessionOpt.get();
            session.setCurrentPage(page);
            session.setIsCoding(isCoding);
            session.setLastActivity(LocalDateTime.now());
            // current_code 필드는 업데이트하지 않음 (CLOB 문제 때문에)
            
            activeSessionRepository.save(session);
        }
        
    } catch (Exception e) {
        System.err.println("=== 세션 업데이트 오류 ===");
        System.err.println("요청 데이터: " + activityData);
        System.err.println("오류: " + e.getMessage());
        System.err.println("=====================");
        // 에러가 발생해도 계속 진행 (세션 업데이트 실패가 전체 기능을 막지 않도록)
    }
}
