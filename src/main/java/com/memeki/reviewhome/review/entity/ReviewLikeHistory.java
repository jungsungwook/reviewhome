package com.memeki.reviewhome.review.entity;

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
@SQLDelete(sql = "UPDATE review_like_history SET is_deleted = true WHERE id = ?")
@Where(clause = "is_deleted = false")
@Table(name = "review_like_history")
@Getter
@Setter
@ToString()
@NoArgsConstructor
public class ReviewLikeHistory {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "review_id")
    private int reviewId;

    @Column(name = "user_id")
    private long userId;

    @Column(name = "created_by")
    private long createdBy;

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
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    @PreRemove
    protected void onDelete() {
        this.deletedAt = LocalDateTime.now();
        this.isDeleted = true;
    }
}
