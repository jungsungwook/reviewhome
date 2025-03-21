package com.memeki.reviewhome.banner.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.memeki.reviewhome.banner.entity.Banner;

import java.util.List;

public interface BannerRepository extends JpaRepository<Banner, Long> {
    List<Banner> findByBannerType(String type);
    List<Banner> findByBannerTypeAndBannerRoute(String type, String route);
    List<Banner> findByBannerTypeAndBannerRouteAndBannerName(String type, String route, String name);
    List<Banner> findByBannerRoute(String route);
    List<Banner> findByBannerRouteAndBannerName(String route, String name);
    List<Banner> findByBannerName(String name);
}
