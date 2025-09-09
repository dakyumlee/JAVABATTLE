package com.javabattle.arena.model;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "TEACHER_MATERIALS")
public class TeacherMaterial {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "teacher_material_seq")
    @SequenceGenerator(name = "teacher_material_seq", sequenceName = "TEACHER_MATERIAL_SEQ", allocationSize = 1)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(name = "CONTENT")
    @Lob
    private String description;

    @Column(name = "MATERIAL_TYPE")
    private String fileType;

    @Column(name = "YOUTUBE_URL")
    private String youtubeUrl;

    @Column(name = "TEACHER_ID")
    private Long uploadedBy;

    @Column(name = "UPLOAD_DATE")
    private LocalDateTime createdAt;

    @Column(name = "CLASS_DATE")
    private LocalDateTime classDate;

    @Column(name = "FILE_NAME")
    private String fileName;

    @Column(name = "FILE_SIZE")
    private Long fileSize;

    @Transient
    private String category;

    @Transient
    private String tags;

    @Transient
    private Boolean shared = false;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getTags() { return tags; }
    public void setTags(String tags) { this.tags = tags; }

    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }

    public Long getFileSize() { return fileSize; }
    public void setFileSize(Long fileSize) { this.fileSize = fileSize; }

    public String getFileType() { return fileType; }
    public void setFileType(String fileType) { this.fileType = fileType; }

    public String getYoutubeUrl() { return youtubeUrl; }
    public void setYoutubeUrl(String youtubeUrl) { this.youtubeUrl = youtubeUrl; }

    public Long getUploadedBy() { return uploadedBy; }
    public void setUploadedBy(Long uploadedBy) { this.uploadedBy = uploadedBy; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getClassDate() { return classDate; }
    public void setClassDate(LocalDateTime classDate) { this.classDate = classDate; }

    public Boolean getShared() { return shared; }
    public void setShared(Boolean shared) { this.shared = shared; }
}