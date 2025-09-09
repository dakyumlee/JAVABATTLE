@GetMapping("/shared-materials")
@ResponseBody
public ResponseEntity<List<TeacherMaterial>> getSharedMaterials() {
    try {
        List<TeacherMaterial> sharedMaterials = materialRepository.findBySharedTrueOrderByCreatedAtDesc();
        return ResponseEntity.ok(sharedMaterials);
    } catch (Exception e) {
        return ResponseEntity.badRequest().build();
    }
}

@GetMapping("/materials/{id}/download")
public ResponseEntity<Resource> downloadMaterial(@PathVariable Long id, HttpServletRequest request) {
    try {
        TeacherMaterial material = materialRepository.findById(id).orElse(null);
        if (material == null || !material.getShared()) {
            return ResponseEntity.notFound().build();
        }
        
        Path filePath = Paths.get("uploads/" + material.getFileName());
        Resource resource = new UrlResource(filePath.toUri());
        
        if (resource.exists()) {
            return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + material.getFileName() + "\"")
                .body(resource);
        } else {
            return ResponseEntity.notFound().build();
        }
    } catch (Exception e) {
        return ResponseEntity.badRequest().build();
    }
}

@GetMapping("/materials/{id}/view")
public ResponseEntity<Resource> viewMaterial(@PathVariable Long id) {
    try {
        TeacherMaterial material = materialRepository.findById(id).orElse(null);
        if (material == null || !material.getShared()) {
            return ResponseEntity.notFound().build();
        }
        
        if (material.getFileType().equals("youtube")) {
            return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(material.getYoutubeUrl()))
                .build();
        }
        
        Path filePath = Paths.get("uploads/" + material.getFileName());
        Resource resource = new UrlResource(filePath.toUri());
        
        if (resource.exists()) {
            return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .body(resource);
        } else {
            return ResponseEntity.notFound().build();
        }
    } catch (Exception e) {
        return ResponseEntity.badRequest().build();
    }
}
