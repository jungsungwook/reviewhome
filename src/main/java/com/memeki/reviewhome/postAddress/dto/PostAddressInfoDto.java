package com.memeki.reviewhome.postAddress.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PostAddressInfoDto {
    private String uuid;
    private int postAddressId;
    private String mainPurpsCdNm;
    private int hhldCnt;
    private int grndFlrCnt;
    private int ugrndFlrCnt;
    private int indrAutoUtcnt;
    private int oudrAutoUtcnt;
    private int indrMechUtcnt;
    private int oudrMechUtcnt;
    private String stcnsDay;
    private String useAprDay;
    private String newPlatPlc;
    private String platPlc;
    private int rideUseElvtCnt;
    private String bldNm;
    private String dongNm;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @JsonCreator
    public PostAddressInfoDto(
            @JsonProperty("uuid") String uuid,
            @JsonProperty("postAddressId") int postAddressId,
            @JsonProperty("mainPurpsCdNm") String mainPurpsCdNm,
            @JsonProperty("hhldCnt") int hhldCnt,
            @JsonProperty("grndFlrCnt") int grndFlrCnt,
            @JsonProperty("ugrndFlrCnt") int ugrndFlrCnt,
            @JsonProperty("indrAutoUtcnt") int indrAutoUtcnt,
            @JsonProperty("oudrAutoUtcnt") int oudrAutoUtcnt,
            @JsonProperty("indrMechUtcnt") int indrMechUtcnt,
            @JsonProperty("oudrMechUtcnt") int oudrMechUtcnt,
            @JsonProperty("stcnsDay") String stcnsDay,
            @JsonProperty("useAprDay") String useAprDay,
            @JsonProperty("newPlatPlc") String newPlatPlc,
            @JsonProperty("platPlc") String platPlc,
            @JsonProperty("rideUseElvtCnt") int rideUseElvtCnt,
            @JsonProperty("bldNm") String bldNm,
            @JsonProperty("dongNm") String dongNm,
            @JsonProperty("createdAt") LocalDateTime createdAt,
            @JsonProperty("updatedAt") LocalDateTime updatedAt
            ) {
        this.uuid = uuid;
        this.postAddressId = postAddressId;
        this.mainPurpsCdNm = mainPurpsCdNm;
        this.hhldCnt = hhldCnt;
        this.grndFlrCnt = grndFlrCnt;
        this.ugrndFlrCnt = ugrndFlrCnt;
        this.indrAutoUtcnt = indrAutoUtcnt;
        this.oudrAutoUtcnt = oudrAutoUtcnt;
        this.indrMechUtcnt = indrMechUtcnt;
        this.oudrMechUtcnt = oudrMechUtcnt;
        this.stcnsDay = stcnsDay;
        this.useAprDay = useAprDay;
        this.newPlatPlc = newPlatPlc;
        this.platPlc = platPlc;
        this.rideUseElvtCnt = rideUseElvtCnt;
        this.bldNm = bldNm;
        this.dongNm = dongNm;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
