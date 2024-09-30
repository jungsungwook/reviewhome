package com.memeki.reviewhome.geo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.memeki.reviewhome.geo.entity.GeoLegalDong;

import java.util.List;

public interface GeoLegalDongRepository extends JpaRepository<GeoLegalDong, String> {
    GeoLegalDong findByBjdCd(String bjdCd);
    GeoLegalDong findBySidoNm(String sidoNm);
    GeoLegalDong findBySigunguNm(String sigunguNm);
    GeoLegalDong findByLegalDongNm(String legalDongNm);
    GeoLegalDong findByLegalLiNm(String legalLiNm);
}
