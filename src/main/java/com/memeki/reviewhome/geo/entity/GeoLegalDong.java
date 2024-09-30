package com.memeki.reviewhome.geo.entity;

import javax.persistence.*;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "geo_legal_dong")
@SQLDelete(sql = "UPDATE geo_legal_dong SET is_deleted = true WHERE id = ?")
@Where(clause = "is_deleted = false")
@Getter
@Setter
@ToString()
@NoArgsConstructor
@Schema(description = "법정동 정보 엔티티")
public class GeoLegalDong {
    @Id
    @Column(name = "bjd_cd")
    @Schema(description = "법정동 코드", example = "1159060500")
    private String bjdCd;

    @Column(name = "sido_nm")
    @Schema(description = "시도명", example = "서울특별시")
    private String sidoNm;

    @Column(name = "sigungu_nm")
    @Schema(description = "시군구명", example = "동작구")
    private String sigunguNm;

    @Column(name = "legal_dong_nm")
    @Schema(description = "법정동명(읍면동)", example = "개포동")
    private String legalDongNm;

    @Column(name = "legal_li_nm")
    @Schema(description = "법정리명", example = "읍면에 있는 행정구역만 표시")
    private String legalLiNm;

    @Column(name = "sort_order")
    @Schema(description = "정렬 순서", example = "1")
    private Integer sortOrder;

    @Column(name = "bjd_type")
    @Schema(description = "법정동 타입(0=시도, 1=시군구, 2=읍면동, 3=리)", example = "0")
    private Integer bjdType;

    @Column(name = "is_deleted")
    @ColumnDefault("false")
    private Boolean isDeleted = false;
}
