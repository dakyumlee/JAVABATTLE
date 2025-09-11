package com.javabattle.arena.service;

import com.javabattle.arena.model.TeacherMaterial;
import com.javabattle.arena.repository.TeacherMaterialRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class TeacherMaterialService {

    @Autowired
    private TeacherMaterialRepository teacherMaterialRepository;

    public TeacherMaterial uploadMaterial(String title, String description, String category, 
                                        String tags, MultipartFile file, Long teacherId) {
        try {
            TeacherMaterial material = new TeacherMaterial();
            material.setTitle(title);
            material.setDescription(description);
            material.setCategory(category);
            material.setTags(tags);
            material.setTeacherId(teacherId);
            material.setCreatedAt(LocalDateTime.now());
            material.setIsShared(false);

            if (file != null && !file.isEmpty()) {
                material.setFileName(file.getOriginalFilename());
                material.setFileSize(file.getSize());
                material.setMaterialType(file.getContentType());
                material.setFileData(file.getBytes());
            } else {
                material.setMaterialType("text");
                material.setContent(description);
            }

            return teacherMaterialRepository.save(material);
        } catch (Exception e) {
            throw new RuntimeException("자료 업로드 실패: " + e.getMessage(), e);
        }
    }

    public List<TeacherMaterial> getAllMaterials() {
        return teacherMaterialRepository.findAllOrderByCreatedAtDesc();
    }

    public List<TeacherMaterial> getSharedMaterials() {
        return teacherMaterialRepository.findSharedMaterials();
    }

    public Optional<TeacherMaterial> getMaterialById(Long id) {
        return teacherMaterialRepository.findById(id);
    }

    public TeacherMaterial shareMaterial(Long id, boolean shared) {
        Optional<TeacherMaterial> materialOpt = teacherMaterialRepository.findById(id);
        if (materialOpt.isPresent()) {
            TeacherMaterial material = materialOpt.get();
            material.setIsShared(shared);
            return teacherMaterialRepository.save(material);
        }
        throw new RuntimeException("자료를 찾을 수 없습니다: " + id);
    }

    public void deleteMaterial(Long id) {
        if (teacherMaterialRepository.existsById(id)) {
            teacherMaterialRepository.deleteById(id);
        } else {
            throw new RuntimeException("자료를 찾을 수 없습니다: " + id);
        }
    }

    public TeacherMaterial updateMaterial(Long id, String title, String description, 
                                        String category, String tags) {
        Optional<TeacherMaterial> materialOpt = teacherMaterialRepository.findById(id);
        if (materialOpt.isPresent()) {
            TeacherMaterial material = materialOpt.get();
            
            if (title != null) material.setTitle(title);
            if (description != null) material.setDescription(description);
            if (category != null) material.setCategory(category);
            if (tags != null) material.setTags(tags);
            
            return teacherMaterialRepository.save(material);
        }
        throw new RuntimeException("자료를 찾을 수 없습니다: " + id);
    }

    public TeacherMaterial addYouTubeLink(String title, String description, String category,
                                        String tags, String url, Long teacherId) {
        TeacherMaterial material = new TeacherMaterial();
        material.setTitle(title);
        material.setDescription(description);
        material.setCategory(category);
        material.setTags(tags);
        material.setYoutubeUrl(url);
        material.setMaterialType("youtube");
        material.setTeacherId(teacherId);
        material.setCreatedAt(LocalDateTime.now());
        material.setIsShared(false);
        
        return teacherMaterialRepository.save(material);
    }
}