package com.javabattle.arena.service;

import com.javabattle.arena.model.Category;
import com.javabattle.arena.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class CategoryService {
    
    @Autowired
    private CategoryRepository categoryRepository;
    
    public List<Category> getAllCategoriesByUserId(Long userId) {
        return categoryRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }
    
    public Category createCategory(Long userId, String name, String color) {
        if (categoryRepository.existsByUserIdAndName(userId, name)) {
            throw new RuntimeException("이미 존재하는 카테고리 이름입니다.");
        }
        
        Category category = new Category();
        category.setUserId(userId);
        category.setName(name);
        category.setColor(color);
        category.setCreatedAt(LocalDateTime.now());
        
        return categoryRepository.save(category);
    }
    
    public Category updateCategory(Long id, String name, String color) {
        Category category = categoryRepository.findById(id).orElse(null);
        if (category != null) {
            if (!category.getName().equals(name) && 
                categoryRepository.existsByUserIdAndName(category.getUserId(), name)) {
                throw new RuntimeException("이미 존재하는 카테고리 이름입니다.");
            }
            
            category.setName(name);
            category.setColor(color);
            return categoryRepository.save(category);
        }
        return null;
    }
    
    public boolean deleteCategory(Long id) {
        if (categoryRepository.existsById(id)) {
            categoryRepository.deleteById(id);
            return true;
        }
        return false;
    }
    
    public Long getCategoriesCount(Long userId) {
        return categoryRepository.countByUserId(userId);
    }
}
