package com.memeki.reviewhome.banner.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.memeki.reviewhome.banner.entity.BannerContent;
import java.util.List;

public interface BannerContentRepository extends JpaRepository<BannerContent, Long> {
    List<BannerContent> findByBannerId(Long bannerId);
}
