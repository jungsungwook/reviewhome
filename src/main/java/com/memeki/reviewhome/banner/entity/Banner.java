package com.memeki.reviewhome.banner.entity;

import javax.persistence.*;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import java.time.LocalDateTime;

@Entity
@Table(name = "banner")
@SQLDelete(sql = "UPDATE banner SET is_deleted = true WHERE id = ?")
@Where(clause = "is_deleted = false")
@Getter
@Setter
@ToString()
@NoArgsConstructor
@Schema(description = "배너 엔티티")
public class Banner {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "배너 ID", example = "1")
    private Long id;

    @Column(name = "banner_type")
    @Schema(description = "배너 타입", example = "promotional")
    private String bannerType;

    @Column(name = "banner_route")
    @Schema(description = "배너 경로", example = "/home")
    private String bannerRoute;

    @Column(name = "banner_name")
    @Schema(description = "배너 이름", example = "Summer Sale")
    private String bannerName;

    @Column(name = "banner_description")
    @Schema(description = "배너 설명", example = "여름 세일 프로모션 배너")
    private String bannerDescription;

    @Column(name = "created_at")
    @Schema(description = "생성 일시", example = "2023-09-27T10:30:00")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @Schema(description = "수정 일시", example = "2023-09-27T15:45:00")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    @Schema(description = "삭제 일시", example = "2023-09-28T09:00:00")
    private LocalDateTime deletedAt;

    @Column(name = "is_deleted")
    @ColumnDefault("false")
    @Schema(description = "삭제 여부", example = "false")
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
