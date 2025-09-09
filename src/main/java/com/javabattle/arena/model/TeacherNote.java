package com.javabattle.arena.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "TEACHER_NOTES")
public class TeacherNote {
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;

@Column(name = "TEACHER_ID", nullable = false)
private Long teacherId;

@Column(name = "TITLE", nullable = false, length = 200)
private String title;

@Column(name = "CONTENT", columnDefinition = "TEXT")
private String content;

@Column(name = "CATEGORY", length = 50)
private String category;

@Column(name = "IS_PINNED")
private Boolean isPinned = false;

@Column(name = "CREATED_AT")
private LocalDateTime createdAt;

@Column(name = "UPDATED_AT")
private LocalDateTime updatedAt;

@PrePersist
protected void onCreate() {
createdAt = LocalDateTime.now();
updatedAt = LocalDateTime.now();
}

@PreUpdate
protected void onUpdate() {
updatedAt = LocalDateTime.now();
}

public TeacherNote() {}

public TeacherNote(Long teacherId, String title, String content, String category) {
this.teacherId = teacherId;
this.title = title;
this.content = content;
this.category = category;
}

public Long getId() { return id; }
public void setId(Long id) { this.id = id; }

public Long getTeacherId() { return teacherId; }
public void setTeacherId(Long teacherId) { this.teacherId = teacherId; }

public String getTitle() { return title; }
public void setTitle(String title) { this.title = title; }

public String getContent() { return content; }
public void setContent(String content) { this.content = content; }

public String getCategory() { return category; }
public void setCategory(String category) { this.category = category; }

public Boolean getIsPinned() { return isPinned; }
public void setIsPinned(Boolean isPinned) { this.isPinned = isPinned; }

public LocalDateTime getCreatedAt() { return createdAt; }
public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

public LocalDateTime getUpdatedAt() { return updatedAt; }
public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}