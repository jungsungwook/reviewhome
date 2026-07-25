package com.memeki.reviewhome.commercial.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@Schema(description = "상권 업소 정보 DTO")
public class CommercialItemDto {
    
    @Schema(description = "상가업소번호", example = "MA010120220800111901")
    private String bizesId;
    
    @Schema(description = "상호명", example = "SBS노래연습장")
    private String bizesNm;
    
    @Schema(description = "지점명", example = "강남점")
    private String brchNm;
    
    @Schema(description = "상권업종대분류코드", example = "R1")
    private String indsLclsCd;
    
    @Schema(description = "상권업종대분류명", example = "예술·스포츠")
    private String indsLclsNm;
    
    @Schema(description = "상권업종중분류코드", example = "R104")
    private String indsMclsCd;
    
    @Schema(description = "상권업종중분류명", example = "유원지·오락")
    private String indsMclsNm;
    
    @Schema(description = "상권업종소분류코드", example = "R10407")
    private String indsSclsCd;
    
    @Schema(description = "상권업종소분류명", example = "노래방")
    private String indsSclsNm;
    
    @Schema(description = "표준산업분류코드", example = "R91223")
    private String ksicCd;
    
    @Schema(description = "표준산업분류명", example = "노래 연습장 운영업")
    private String ksicNm;
    
    @Schema(description = "시도코드", example = "11")
    private String ctprvnCd;
    
    @Schema(description = "시도명", example = "서울특별시")
    private String ctprvnNm;
    
    @Schema(description = "시군구코드", example = "11710")
    private String signguCd;
    
    @Schema(description = "시군구명", example = "송파구")
    private String signguNm;
    
    @Schema(description = "행정동코드", example = "11710632")
    private String adongCd;
    
    @Schema(description = "행정동명", example = "가락2동")
    private String adongNm;
    
    @Schema(description = "법정동코드", example = "1171010700")
    private String ldongCd;
    
    @Schema(description = "법정동명", example = "가락동")
    private String ldongNm;
    
    @Schema(description = "PNU코드", example = "1171010700101740027")
    private String lnoCd;
    
    @Schema(description = "대지구분코드", example = "1")
    private String plotSctCd;
    
    @Schema(description = "대지구분명", example = "대지")
    private String plotSctNm;
    
    @Schema(description = "지번본번지", example = "174")
    private Integer lnoMnno;
    
    @Schema(description = "지번부번지", example = "27")
    private Integer lnoSlno;
    
    @Schema(description = "지번주소", example = "서울특별시 송파구 가락동 174-27")
    private String lnoAdr;
    
    @Schema(description = "도로명코드", example = "117104169403")
    private String rdnmCd;
    
    @Schema(description = "도로명", example = "서울특별시 송파구 오금로46길")
    private String rdnm;
    
    @Schema(description = "건물본번지", example = "22")
    private Integer bldMnno;
    
    @Schema(description = "건물부번지", example = "1")
    private String bldSlno;
    
    @Schema(description = "건물관리번호", example = "1171010700101740027005374")
    private String bldMngNo;
    
    @Schema(description = "건물명", example = "가락프라자")
    private String bldNm;
    
    @Schema(description = "도로명주소", example = "서울특별시 송파구 오금로46길 22")
    private String rdnmAdr;
    
    @Schema(description = "구우편번호", example = "138811")
    private String oldZipcd;
    
    @Schema(description = "신우편번호", example = "05769")
    private String newZipcd;
    
    @Schema(description = "동정보", example = "101")
    private String dongNo;
    
    @Schema(description = "층정보", example = "지")
    private String flrNo;
    
    @Schema(description = "호정보", example = "1001")
    private String hoNo;
    
    @Schema(description = "경도", example = "127.13487420192")
    private Double lon;
    
    @Schema(description = "위도", example = "37.495687165975")
    private Double lat;
}
