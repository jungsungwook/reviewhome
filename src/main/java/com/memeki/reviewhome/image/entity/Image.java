package com.memeki.reviewhome.image.entity;

import java.time.LocalDateTime;

import javax.persistence.*;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "image")
@SQLDelete(sql = "UPDATE image SET is_deleted = true WHERE id = ?")
@Where(clause = "is_deleted = false")
@Getter
@Setter
@ToString()
@NoArgsConstructor
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "uuid")
    private String uuid;

    @Column(name = "original_name")
    private String originalName;

    @Column(name = "saved_name")
    private String savedName;

    @Column(name = "url")
    private String url;

    @Column(name = "size")
    private Long size;

    @Column(name = "extension")
    private String extension;

    @Column(name = "created_by")
    private Long createdBy;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column(name = "is_deleted")
    @ColumnDefault("false")
    private Boolean isDeleted = false;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
