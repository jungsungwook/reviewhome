package com.memeki.reviewhome.image.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.memeki.reviewhome.image.entity.Image;

public interface ImageRepository extends JpaRepository<Image, Long> {
    public Image findImageByUuid(String uuid);
}
