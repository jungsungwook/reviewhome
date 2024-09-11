package com.memeki.reviewhome.community.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import javax.persistence.*;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "community")
@SQLDelete(sql = "UPDATE community SET is_deleted = true WHERE id = ?")
@Where(clause = "is_deleted = false")
@Getter
@Setter
@ToString()
@NoArgsConstructor
public class Community {
    @Id
    @Column(name = "uuid")
    private String uuid;

    /*
     * building : 건물
     * town : 동네
     */
    @Column(name = "type")
    private String type;

    /*
     * 호환성을 위해 target_id를 String으로 선언하였으니 형변환에 유의바람.
     */
    @Column(name = "target_id")
    private String targetId;

    @Column(name = "type2")
    private String type2;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "is_password")
    @ColumnDefault("false")
    private Boolean isPassword = false;

    @JsonIgnore
    @Column(name = "password")
    private String password;

    @Column(name = "geo_features_id")
    private int geoFeaturesId;

    @Column(name = "geo_features_name")
    private String geoFeaturesName;

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
        if (uuid == null) {
            uuid = UUID.randomUUID().toString().replaceAll("-", "");
        }

        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    @Transient
    private Boolean isEnter = false;

    @Transient
    private Boolean isManager = false;

    @Transient
    private Boolean isOwner = false;

    @Transient
    private Integer userCount = 0;
}
