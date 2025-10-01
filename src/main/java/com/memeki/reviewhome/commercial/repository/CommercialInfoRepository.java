package com.memeki.reviewhome.commercial.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.memeki.reviewhome.commercial.entity.CommercialInfo;

import java.util.List;

public interface CommercialInfoRepository extends JpaRepository<CommercialInfo, Long> {
    
    /**
     * 특정 건물 UUID로 상권 정보 조회
     */
    List<CommercialInfo> findByPostAddressInfoUuid(String postAddressInfoUuid);
    
    /**
     * 상가업소번호로 상권 정보 조회
     */
    CommercialInfo findByBizesId(String bizesId);
    
    /**
     * 상호명으로 상권 정보 조회
     */
    List<CommercialInfo> findByBizesNmContaining(String bizesNm);
    
    /**
     * 업종별 상권 정보 조회
     */
    List<CommercialInfo> findByIndsLclsCd(String indsLclsCd);
    List<CommercialInfo> findByIndsMclsCd(String indsMclsCd);
    List<CommercialInfo> findByIndsSclsCd(String indsSclsCd);
    
    /**
     * 지역별 상권 정보 조회
     */
    List<CommercialInfo> findByCtprvnCdAndSignguCd(String ctprvnCd, String signguCd);
    List<CommercialInfo> findByAdongCd(String adongCd);
    List<CommercialInfo> findByLdongCd(String ldongCd);
    
    /**
     * 특정 건물 UUID의 상권 정보 존재 여부 확인
     */
    boolean existsByPostAddressInfoUuid(String postAddressInfoUuid);
    
    /**
     * 특정 건물 UUID의 상권 정보 삭제
     */
    void deleteByPostAddressInfoUuid(String postAddressInfoUuid);
    
    /**
     * 좌표 범위 내의 상권 정보 조회 (반경 검색)
     */
    @Query("SELECT c FROM CommercialInfo c WHERE " +
           "(6371 * acos(cos(radians(:lat)) * cos(radians(c.lat)) * " +
           "cos(radians(c.lon) - radians(:lon)) + sin(radians(:lat)) * " +
           "sin(radians(c.lat)))) <= :radius")
    List<CommercialInfo> findByLocationWithinRadius(@Param("lat") double lat, 
                                                   @Param("lon") double lon, 
                                                   @Param("radius") double radius);
}
