package com.javabattle.arena.service;

import com.javabattle.arena.model.TeacherMaterial;
import com.javabattle.arena.repository.TeacherMaterialRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TeacherMaterialService {

    @Autowired
    private TeacherMaterialRepository teacherMaterialRepository;

    public List<TeacherMaterial> getAllMaterials() {
        return teacherMaterialRepository.findAllOrderByCreatedAtDesc();
    }

    public List<TeacherMaterial> getMaterialsByCategory(String category) {
        return teacherMaterialRepository.findAllOrderByCreatedAtDesc()
                .stream()
                .filter(material -> category.equals(material.getCategory()))
                .collect(Collectors.toList());
    }

    public List<TeacherMaterial> getSharedMaterials() {
        return teacherMaterialRepository.findAllOrderByCreatedAtDesc()
                .stream()
                .filter(material -> Boolean.TRUE.equals(material.getShared()))
                .collect(Collectors.toList());
    }

    public List<TeacherMaterial> getMaterialsByTeacher(Long teacherId) {
        return teacherMaterialRepository.findByTeacherId(teacherId);
    }

    public List<TeacherMaterial> getSharedMaterialsByTeacher(Long teacherId) {
        return teacherMaterialRepository.findByTeacherId(teacherId)
                .stream()
                .filter(material -> Boolean.TRUE.equals(material.getShared()))
                .collect(Collectors.toList());
    }

    public List<TeacherMaterial> getMaterialsByType(String fileType) {
        return teacherMaterialRepository.findByFileType(fileType);
    }

    public TeacherMaterial saveMaterial(TeacherMaterial material) {
        return teacherMaterialRepository.save(material);
    }

    public void deleteMaterial(Long id) {
        teacherMaterialRepository.deleteById(id);
    }

    public Optional<TeacherMaterial> findById(Long id) {
        return teacherMaterialRepository.findById(id);
    }

    public TeacherMaterial findByIdOrNull(Long id) {
        return teacherMaterialRepository.findById(id).orElse(null);
    }

    public boolean existsById(Long id) {
        return teacherMaterialRepository.existsById(id);
    }

    public long countMaterialsByTeacher(Long teacherId) {
        return teacherMaterialRepository.findByTeacherId(teacherId).size();
    }

    public long countSharedMaterialsByTeacher(Long teacherId) {
        return teacherMaterialRepository.findByTeacherId(teacherId)
                .stream()
                .filter(material -> Boolean.TRUE.equals(material.getShared()))
                .count();
    }
}