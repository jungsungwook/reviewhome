package com.memeki.reviewhome.postAddress.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import javax.persistence.*;

import com.memeki.reviewhome.postAddress.dto.GetBrTitleInfoResponseDto.ItemDto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "post_address_info")
@Getter
@Setter
@ToString()
@NoArgsConstructor
public class PostAddressInfo {
    @Id
    @Column(name = "uuid")
    private String uuid;

    @Column(name = "post_address_id")
    private int postAddressId;

    @Column(name = "mainPurpsCdNm")
    private String mainPurpsCdNm;

    @Column(name = "hhldCnt")
    private int hhldCnt;

    @Column(name = "grndFlrCnt")
    private int grndFlrCnt;

    @Column(name = "ugrndFlrCnt")
    private int ugrndFlrCnt;

    @Column(name = "indrAutoUtcnt")
    private int indrAutoUtcnt;

    @Column(name = "oudrAutoUtcnt")
    private int oudrAutoUtcnt;

    @Column(name = "indrMechUtcnt")
    private int indrMechUtcnt;

    @Column(name = "oudrMechUtcnt")
    private int oudrMechUtcnt;

    @Column(name = "stcnsDay")
    private String stcnsDay;

    @Column(name = "useAprDay")
    private String useAprDay;

    @Column(name = "newPlatPlc")
    private String newPlatPlc;

    @Column(name = "platPlc")
    private String platPlc;

    @Column(name = "rideUseElvtCnt")
    private int rideUseElvtCnt;

    @Column(name = "bldNm")
    private String bldNm;

    @Column(name = "dongNm")
    private String dongNm;

    @Column(name = "geo_features_id")
    private int geoFeaturesId;

    @Column(name = "geo_features_name")
    private String geoFeaturesName;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        if (uuid == null) {
            uuid = UUID.randomUUID().toString().replaceAll("-", "");
        }

        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public void copyFromItemDto(ItemDto item) {
        this.mainPurpsCdNm = item.getMainPurpsCdNm();
        this.hhldCnt = item.getHhldCnt();
        this.grndFlrCnt = item.getGrndFlrCnt();
        this.ugrndFlrCnt = item.getUgrndFlrCnt();
        this.indrAutoUtcnt = item.getIndrAutoUtcnt();
        this.oudrAutoUtcnt = item.getOudrAutoUtcnt();
        this.indrMechUtcnt = item.getIndrMechUtcnt();
        this.oudrMechUtcnt = item.getOudrMechUtcnt();
        this.stcnsDay = item.getStcnsDay();
        this.useAprDay = item.getUseAprDay();
        this.newPlatPlc = item.getNewPlatPlc();
        this.platPlc = item.getPlatPlc();
        this.rideUseElvtCnt = item.getRideUseElvtCnt();
        this.bldNm = item.getBldNm();
        this.dongNm = item.getDongNm();
    }

    public ItemDto toItemDto() {
        return ItemDto.builder()
                .mainPurpsCdNm(mainPurpsCdNm)
                .hhldCnt(hhldCnt)
                .grndFlrCnt(grndFlrCnt)
                .ugrndFlrCnt(ugrndFlrCnt)
                .indrAutoUtcnt(indrAutoUtcnt)
                .oudrAutoUtcnt(oudrAutoUtcnt)
                .indrMechUtcnt(indrMechUtcnt)
                .oudrMechUtcnt(oudrMechUtcnt)
                .stcnsDay(stcnsDay)
                .useAprDay(useAprDay)
                .newPlatPlc(newPlatPlc)
                .platPlc(platPlc)
                .rideUseElvtCnt(rideUseElvtCnt)
                .bldNm(bldNm)
                .dongNm(dongNm)
                .build();
    }

    public PostAddressInfo orElseThrow(Object object) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'orElseThrow'");
    }
}
