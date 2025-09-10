@PostMapping("/shared-materials")
public ResponseEntity<Map<String, Object>> shareTeacherMaterial(
        @RequestParam("title") String title,
        @RequestParam("content") String content,
        @RequestParam(value = "file", required = false) MultipartFile file) {
    
    try {
        TeacherMaterial material = new TeacherMaterial();
        material.setTitle(title);
        material.setContent(content);
        material.setCreatedAt(LocalDateTime.now());
        material.setClassDate(LocalDateTime.now());
        material.setTeacherId(1L);
        material.setIsShared(true);
        
        if (file != null && !file.isEmpty()) {
            material.setFileName(file.getOriginalFilename());
            material.setFileSize(file.getSize());
            material.setMaterialType(file.getContentType());
        } else {
            material.setMaterialType("text");
        }

        TeacherMaterial saved = teacherMaterialRepository.save(material);

        Map<String, Object> materialData = new HashMap<>();
        materialData.put("type", "NEW_MATERIAL");
        materialData.put("title", saved.getTitle());
        materialData.put("content", saved.getContent());
        materialData.put("materialType", saved.getMaterialType());
        materialData.put("from", "teacher");
        materialData.put("timestamp", LocalDateTime.now());
        materialData.put("materialId", saved.getId());

        messagingTemplate.convertAndSend("/topic/teacher-announcements", materialData);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "자료가 성공적으로 공유되었습니다.");
        response.put("materialId", saved.getId());
        
        return ResponseEntity.ok(response);
        
    } catch (Exception e) {
        e.printStackTrace();
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", "자료 공유 중 오류가 발생했습니다: " + e.getMessage());
        return ResponseEntity.badRequest().body(response);
    }
}

@GetMapping("/materials/shared")
public ResponseEntity<Map<String, Object>> getSharedMaterials() {
    try {
        List<TeacherMaterial> materials = teacherMaterialRepository.findSharedMaterials();
        
        List<Map<String, Object>> materialList = materials.stream().map(material -> {
            Map<String, Object> data = new HashMap<>();
            data.put("id", material.getId());
            data.put("title", material.getTitle());
            data.put("content", material.getContent());
            data.put("materialType", material.getMaterialType());
            data.put("fileName", material.getFileName());
            data.put("fileSize", material.getFileSize());
            data.put("youtubeUrl", material.getYoutubeUrl());
            data.put("createdAt", material.getCreatedAt());
            data.put("classDate", material.getClassDate());
            return data;
        }).collect(Collectors.toList());
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("materials", materialList);
        response.put("totalCount", materialList.size());
        
        return ResponseEntity.ok(response);
    } catch (Exception e) {
        e.printStackTrace();
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", "공유 자료 조회 중 오류가 발생했습니다: " + e.getMessage());
        return ResponseEntity.badRequest().body(response);
    }
}