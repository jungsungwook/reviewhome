package com.memeki.reviewhome.postAddress.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class SeoulBuildingResponseDto {
    private VBigDjrTitle vBigDjrTitle;

    @JsonCreator
    public SeoulBuildingResponseDto(@JsonProperty("vBigDjrTitle") VBigDjrTitle vBigDjrTitle) {
        this.vBigDjrTitle = vBigDjrTitle;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    @ToString
    public static class VBigDjrTitle {
        @JsonProperty("list_total_count")
        private int listTotalCount;
        
        @JsonProperty("RESULT")
        private Result result;
        
        private List<Row> row;

        @JsonCreator
        public VBigDjrTitle(
                @JsonProperty("list_total_count") int listTotalCount,
                @JsonProperty("RESULT") Result result,
                @JsonProperty("row") List<Row> row) {
            this.listTotalCount = listTotalCount;
            this.result = result;
            this.row = row;
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    @ToString
    public static class Result {
        @JsonProperty("CODE")
        private String code;
        
        @JsonProperty("MESSAGE")
        private String message;

        @JsonCreator
        public Result(
                @JsonProperty("CODE") String code,
                @JsonProperty("MESSAGE") String message) {
            this.code = code;
            this.message = message;
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    @ToString
    public static class Row {
        @JsonProperty("PLAT_PLC")
        private String platPlc; // 대지위치 (구주소)
        
        @JsonProperty("SGG_CD_NM")
        private String sggCdNm; // 시군구코드명
        
        @JsonProperty("STDG_CD_NM")
        private String stdgCdNm; // 법정동코드명
        
        @JsonProperty("MN_LOTNO")
        private String mnLotno; // 주지번
        
        @JsonProperty("SUB_LOTNO")
        private String subLotno; // 부지번
        
        @JsonProperty("NA_ROAD_CD_NM")
        private String naRoadCdNm; // 새주소도로코드명
        
        @JsonProperty("NA_STDG_CD_NM")
        private String naStdgCdNm; // 새주소법정동코드명
        
        @JsonProperty("NA_MN_LOTNO")
        private String naMnLotno; // 새주소주지번
        
        @JsonProperty("NA_SUB_LOTNO")
        private String naSubLotno; // 새주소부지번
        
        @JsonProperty("BDRG_SN")
        private String bdrgSn; // 건축물대장일련번호
        
        @JsonProperty("DNG_NM")
        private String dngNm; // 동명
        
        @JsonProperty("MN_USG_CD_NM")
        private String mnUsgCdNm; // 주용도코드명
        
        @JsonProperty("HH_CNT")
        private int hhCnt; // 세대수
        
        @JsonProperty("GRND_NOFL")
        private String grndNofl; // 지상층수
        
        @JsonProperty("UDGD_NOFL")
        private String udgdNofl; // 지하층수
        
        @JsonProperty("INDR_MCNCL_CNTOM")
        private int indrMcnclCntom; // 옥내기계식대수
        
        @JsonProperty("OTDR_MCNCL_CNTOM")
        private int otdrMcnclCntom; // 옥외기계식대수
        
        @JsonProperty("INDR_SFPRPL_CNTOM")
        private int indrSfprplCntom; // 옥내자주식대수
        
        @JsonProperty("OTDR_SFPRPL_CNTOM")
        private int otdrSfprplCntom; // 옥외자주식대수
        
        @JsonProperty("BGNCST_YMD")
        private String bgncstYmd; // 착공일자
        
        @JsonProperty("USE_APRV_YMD")
        private String useAprvYmd; // 사용승인일자
        
        @JsonProperty("PSNGR_ELVTR_CNT")
        private int psngrElvtrCnt; // 승용승강기수

        @JsonCreator
        public Row(
                @JsonProperty("PLAT_PLC") String platPlc,
                @JsonProperty("SGG_CD_NM") String sggCdNm,
                @JsonProperty("STDG_CD_NM") String stdgCdNm,
                @JsonProperty("MN_LOTNO") String mnLotno,
                @JsonProperty("SUB_LOTNO") String subLotno,
                @JsonProperty("NA_ROAD_CD_NM") String naRoadCdNm,
                @JsonProperty("NA_STDG_CD_NM") String naStdgCdNm,
                @JsonProperty("NA_MN_LOTNO") String naMnLotno,
                @JsonProperty("NA_SUB_LOTNO") String naSubLotno,
                @JsonProperty("BDRG_SN") String bdrgSn,
                @JsonProperty("DNG_NM") String dngNm,
                @JsonProperty("MN_USG_CD_NM") String mnUsgCdNm,
                @JsonProperty("HH_CNT") int hhCnt,
                @JsonProperty("GRND_NOFL") String grndNofl,
                @JsonProperty("UDGD_NOFL") String udgdNofl,
                @JsonProperty("INDR_MCNCL_CNTOM") int indrMcnclCntom,
                @JsonProperty("OTDR_MCNCL_CNTOM") int otdrMcnclCntom,
                @JsonProperty("INDR_SFPRPL_CNTOM") int indrSfprplCntom,
                @JsonProperty("OTDR_SFPRPL_CNTOM") int otdrSfprplCntom,
                @JsonProperty("BGNCST_YMD") String bgncstYmd,
                @JsonProperty("USE_APRV_YMD") String useAprvYmd,
                @JsonProperty("PSNGR_ELVTR_CNT") int psngrElvtrCnt) {
            this.platPlc = platPlc;
            this.sggCdNm = sggCdNm;
            this.stdgCdNm = stdgCdNm;
            this.mnLotno = mnLotno;
            this.subLotno = subLotno;
            this.naRoadCdNm = naRoadCdNm;
            this.naStdgCdNm = naStdgCdNm;
            this.naMnLotno = naMnLotno;
            this.naSubLotno = naSubLotno;
            this.bdrgSn = bdrgSn;
            this.dngNm = dngNm;
            this.mnUsgCdNm = mnUsgCdNm;
            this.hhCnt = hhCnt;
            this.grndNofl = grndNofl;
            this.udgdNofl = udgdNofl;
            this.indrMcnclCntom = indrMcnclCntom;
            this.otdrMcnclCntom = otdrMcnclCntom;
            this.indrSfprplCntom = indrSfprplCntom;
            this.otdrSfprplCntom = otdrSfprplCntom;
            this.bgncstYmd = bgncstYmd;
            this.useAprvYmd = useAprvYmd;
            this.psngrElvtrCnt = psngrElvtrCnt;
        }
        
        // 서울시 API 응답을 기존 ItemDto 형식으로 변환하는 헬퍼 메소드
        public GetBrTitleInfoResponseDto.ItemDto toItemDto() {
            // 새주소 생성
            String newPlatPlc = "";
            if (naRoadCdNm != null && !naRoadCdNm.isEmpty()) {
                newPlatPlc = sggCdNm + " " + naStdgCdNm + " " + naRoadCdNm + " " + naMnLotno;
                if (naSubLotno != null && !naSubLotno.isEmpty() && !naSubLotno.equals("0") && !naSubLotno.equals("0000")) {
                    newPlatPlc += "-" + naSubLotno;
                }
            }
            
            return GetBrTitleInfoResponseDto.ItemDto.builder()
                    .mainPurpsCdNm(mnUsgCdNm != null ? mnUsgCdNm : "")
                    .hhldCnt(hhCnt)
                    .grndFlrCnt(grndNofl != null && !grndNofl.isEmpty() ? parseIntSafe(grndNofl) : 0)
                    .ugrndFlrCnt(udgdNofl != null && !udgdNofl.isEmpty() ? parseIntSafe(udgdNofl) : 0)
                    .indrAutoUtcnt(indrSfprplCntom)
                    .oudrAutoUtcnt(otdrSfprplCntom)
                    .indrMechUtcnt(indrMcnclCntom)
                    .oudrMechUtcnt(otdrMcnclCntom)
                    .stcnsDay(bgncstYmd != null ? bgncstYmd : "")
                    .useAprDay(useAprvYmd != null ? useAprvYmd : "")
                    .newPlatPlc(newPlatPlc)
                    .platPlc(platPlc != null ? platPlc : "")
                    .rideUseElvtCnt(psngrElvtrCnt)
                    .bldNm("") // 서울시 API는 건물명을 제공하지 않음
                    .dongNm(dngNm != null ? dngNm : "")
                    .bun(mnLotno != null ? mnLotno : "")
                    .ji(subLotno != null ? subLotno : "")
                    .build();
        }
        
        private int parseIntSafe(String value) {
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException e) {
                return 0;
            }
        }
    }
}

