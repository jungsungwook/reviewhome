package com.memeki.reviewhome.postAddress.entity;

import java.time.LocalDateTime;

import javax.persistence.*;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "post_address")
@Getter
@Setter
@ToString()
@NoArgsConstructor
public class PostAddress {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "sigunguCd")
    private String sigunguCd;

    @Column(name = "bjdongCd")
    private String bjdongCd;

    @Column(name = "bun")
    private String bun;

    @Column(name = "ji")
    private String ji;

    @Column(name = "newPlatPlc")
    private String newPlatPlc;

    @Column(name = "isMultiple")
    private boolean isMultiple;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

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
