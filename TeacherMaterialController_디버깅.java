@GetMapping("/materials")
@ResponseBody
public ResponseEntity<List<TeacherMaterial>> getMaterials(HttpServletRequest request) {
    try {
        System.out.println("=== getMaterials 호출됨 ===");
        String token = extractToken(request);
        System.out.println("추출된 토큰: " + token);
        
        if (token == null) {
            System.out.println("토큰이 null입니다");
            return ResponseEntity.badRequest().build();
        }
        
        Long userId = jwtUtil.extractUserId(token);
        System.out.println("추출된 사용자 ID: " + userId);
        
        List<TeacherMaterial> materials = materialRepository.findByUploadedByOrderByCreatedAtDesc(userId);
        System.out.println("조회된 자료 수: " + materials.size());
        
        return ResponseEntity.ok(materials);
    } catch (Exception e) {
        System.out.println("getMaterials 오류: " + e.getMessage());
        e.printStackTrace();
        return ResponseEntity.badRequest().build();
    }
}

@PostMapping("/materials/upload")
@ResponseBody
public ResponseEntity<?> uploadMaterial(@RequestBody Map<String, String> request,
                                      HttpServletRequest httpRequest) {
    try {
        System.out.println("=== uploadMaterial 호출됨 ===");
        System.out.println("요청 데이터: " + request);
        
        String token = extractToken(httpRequest);
        System.out.println("추출된 토큰: " + token);
        
        if (token == null) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "토큰이 없습니다"));
        }
        
        Long userId = jwtUtil.extractUserId(token);
        System.out.println("추출된 사용자 ID: " + userId);

        TeacherMaterial material = new TeacherMaterial();
        material.setTitle(request.get("title"));
        material.setDescription(request.get("description"));
        material.setCategory(request.get("category"));
        material.setTags(request.get("tags"));
        material.setFileType("text");
        material.setUploadedBy(userId);
        material.setCreatedAt(LocalDateTime.now());
        material.setShared(false);

        materialRepository.save(material);
        System.out.println("자료 저장 완료");

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "자료가 성공적으로 업로드되었습니다.");
        response.put("materialId", material.getId());

        return ResponseEntity.ok(response);
    } catch (Exception e) {
        System.out.println("uploadMaterial 오류: " + e.getMessage());
        e.printStackTrace();
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", "업로드 중 오류가 발생했습니다: " + e.getMessage());
        return ResponseEntity.badRequest().body(response);
    }
}
