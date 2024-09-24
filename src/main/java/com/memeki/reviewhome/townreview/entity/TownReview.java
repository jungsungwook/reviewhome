package com.memeki.reviewhome.townreview.entity;

import java.time.LocalDateTime;

import javax.persistence.*;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Entity
@Table(name = "townreview")
@Getter
@Setter
@ToString()
@NoArgsConstructor
@SQLDelete(sql = "UPDATE townreview SET is_deleted = true WHERE id = ?")
@Where(clause = "is_deleted = false")

public class TownReview {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "type")
    private String type;

    /*
     * 호환성을 위해 target_id를 String으로 선언하였으니 형변환에 유의바람.
     */
    @Column(name = "target_id")
    private String targetId;

    @Column(name = "created_by")
    private long createdBy;//String -> long

    @Column(name="content")
    private String content;
    @Column(name="title")
    private String title;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted=false;



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
}
