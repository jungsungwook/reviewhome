package com.memeki.reviewhome.banner.entity;

import javax.persistence.*;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import java.time.LocalDateTime;

@Entity
@Table(name = "banner_content")
@SQLDelete(sql = "UPDATE banner_content SET is_deleted = true WHERE id = ?")
@Where(clause = "is_deleted = false")
@Getter
@Setter
@ToString()
@NoArgsConstructor
public class BannerContent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "banner_id")
    private Long bannerId;

    @Column(name = "content_type")
    private Integer contentType;

    @Column(name = "content")
    private String content;

    @Column(name = "content_order")
    private Integer contentOrder;

    @Column(name = "content_link")
    private String contentLink;

    @Column(name = "content_size_x")
    private Integer contentSizeX;

    @Column(name = "content_size_y")
    private Integer contentSizeY;

    @Column(name = "content_name")
    private String contentName;

    @Column(name = "content_description")
    private String contentDescription;

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
