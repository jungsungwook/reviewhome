package com.memeki.reviewhome.commercial.entity;

import javax.persistence.*;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import java.time.LocalDateTime;

@Entity
@Table(name = "commercial_info")
@Getter
@Setter
@ToString()
@NoArgsConstructor
@Schema(description = "상권 정보 엔티티")
public class CommercialInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "상권 정보 ID", example = "1")
    private Long id;

    @Column(name = "post_address_info_uuid")
    @Schema(description = "건물 정보 UUID", example = "abc123def456")
    private String postAddressInfoUuid;

    @Column(name = "bizes_id")
    @Schema(description = "상가업소번호", example = "MA010120220800111901")
    private String bizesId;

    @Column(name = "bizes_nm")
    @Schema(description = "상호명", example = "SBS노래연습장")
    private String bizesNm;

    @Column(name = "brch_nm")
    @Schema(description = "지점명", example = "강남점")
    private String brchNm;

    @Column(name = "inds_lcls_cd")
    @Schema(description = "상권업종대분류코드", example = "R1")
    private String indsLclsCd;

    @Column(name = "inds_lcls_nm")
    @Schema(description = "상권업종대분류명", example = "예술·스포츠")
    private String indsLclsNm;

    @Column(name = "inds_mcls_cd")
    @Schema(description = "상권업종중분류코드", example = "R104")
    private String indsMclsCd;

    @Column(name = "inds_mcls_nm")
    @Schema(description = "상권업종중분류명", example = "유원지·오락")
    private String indsMclsNm;

    @Column(name = "inds_scls_cd")
    @Schema(description = "상권업종소분류코드", example = "R10407")
    private String indsSclsCd;

    @Column(name = "inds_scls_nm")
    @Schema(description = "상권업종소분류명", example = "노래방")
    private String indsSclsNm;

    @Column(name = "ksic_cd")
    @Schema(description = "표준산업분류코드", example = "R91223")
    private String ksicCd;

    @Column(name = "ksic_nm")
    @Schema(description = "표준산업분류명", example = "노래 연습장 운영업")
    private String ksicNm;

    @Column(name = "ctprvn_cd")
    @Schema(description = "시도코드", example = "11")
    private String ctprvnCd;

    @Column(name = "ctprvn_nm")
    @Schema(description = "시도명", example = "서울특별시")
    private String ctprvnNm;

    @Column(name = "signgu_cd")
    @Schema(description = "시군구코드", example = "11710")
    private String signguCd;

    @Column(name = "signgu_nm")
    @Schema(description = "시군구명", example = "송파구")
    private String signguNm;

    @Column(name = "adong_cd")
    @Schema(description = "행정동코드", example = "11710632")
    private String adongCd;

    @Column(name = "adong_nm")
    @Schema(description = "행정동명", example = "가락2동")
    private String adongNm;

    @Column(name = "ldong_cd")
    @Schema(description = "법정동코드", example = "1171010700")
    private String ldongCd;

    @Column(name = "ldong_nm")
    @Schema(description = "법정동명", example = "가락동")
    private String ldongNm;

    @Column(name = "lno_cd")
    @Schema(description = "PNU코드", example = "1171010700101740027")
    private String lnoCd;

    @Column(name = "plot_sct_cd")
    @Schema(description = "대지구분코드", example = "1")
    private String plotSctCd;

    @Column(name = "plot_sct_nm")
    @Schema(description = "대지구분명", example = "대지")
    private String plotSctNm;

    @Column(name = "lno_mnno")
    @Schema(description = "지번본번지", example = "174")
    private Integer lnoMnno;

    @Column(name = "lno_slno")
    @Schema(description = "지번부번지", example = "27")
    private Integer lnoSlno;

    @Column(name = "lno_adr")
    @Schema(description = "지번주소", example = "서울특별시 송파구 가락동 174-27")
    private String lnoAdr;

    @Column(name = "rdnm_cd")
    @Schema(description = "도로명코드", example = "117104169403")
    private String rdnmCd;

    @Column(name = "rdnm")
    @Schema(description = "도로명", example = "서울특별시 송파구 오금로46길")
    private String rdnm;

    @Column(name = "bld_mnno")
    @Schema(description = "건물본번지", example = "22")
    private Integer bldMnno;

    @Column(name = "bld_slno")
    @Schema(description = "건물부번지", example = "1")
    private String bldSlno;

    @Column(name = "bld_mng_no")
    @Schema(description = "건물관리번호", example = "1171010700101740027005374")
    private String bldMngNo;

    @Column(name = "bld_nm")
    @Schema(description = "건물명", example = "가락프라자")
    private String bldNm;

    @Column(name = "rdnm_adr")
    @Schema(description = "도로명주소", example = "서울특별시 송파구 오금로46길 22")
    private String rdnmAdr;

    @Column(name = "old_zipcd")
    @Schema(description = "구우편번호", example = "138811")
    private String oldZipcd;

    @Column(name = "new_zipcd")
    @Schema(description = "신우편번호", example = "05769")
    private String newZipcd;

    @Column(name = "dong_no")
    @Schema(description = "동정보", example = "101")
    private String dongNo;

    @Column(name = "flr_no")
    @Schema(description = "층정보", example = "지")
    private String flrNo;

    @Column(name = "ho_no")
    @Schema(description = "호정보", example = "1001")
    private String hoNo;

    @Column(name = "lon")
    @Schema(description = "경도", example = "127.13487420192")
    private Double lon;

    @Column(name = "lat")
    @Schema(description = "위도", example = "37.495687165975")
    private Double lat;

    @Column(name = "created_at")
    @Schema(description = "생성 일시", example = "2023-09-27T10:30:00")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @Schema(description = "수정 일시", example = "2023-09-27T15:45:00")
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
