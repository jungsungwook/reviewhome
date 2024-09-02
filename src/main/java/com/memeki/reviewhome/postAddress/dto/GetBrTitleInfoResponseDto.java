package com.memeki.reviewhome.postAddress.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.memeki.reviewhome.postAddress.utils.ItemOrItemListDeserializer;
import com.memeki.reviewhome.postAddress.utils.ItemsOrStringDeserializer;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class GetBrTitleInfoResponseDto {
    private Response response;

    @JsonCreator
    public GetBrTitleInfoResponseDto(@JsonProperty("response") Response response) {
        this.response = response;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Response {
        private Body body;

        @JsonCreator
        public Response(@JsonProperty("body") Body body) {
            this.body = body;
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Body {
        @JsonDeserialize(using = ItemsOrStringDeserializer.class)
        private Item items;
        private int numOfRows;
        private int pageNo;
        private int totalCount;

        @JsonCreator
        public Body(@JsonProperty("items") Item items,
                    @JsonProperty("numOfRows") int numOfRows,
                    @JsonProperty("pageNo") int pageNo,
                    @JsonProperty("totalCount") int totalCount) {
            this.items = items;
            this.numOfRows = numOfRows;
            this.pageNo = pageNo;
            this.totalCount = totalCount;
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Item {
        @JsonDeserialize(using = ItemOrItemListDeserializer.class)
        private List<ItemDto> item;

        @JsonCreator
        public Item(@JsonProperty("item") List<ItemDto> item) {
            this.item = item;
        }
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ItemDto {
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
        private String bun;
        private String ji;

        @Override
        public String toString() {
            return "ItemDto{" +
                    "mainPurpsCdNm='" + mainPurpsCdNm + '\'' +
                    ", hhldCnt=" + hhldCnt +
                    ", grndFlrCnt=" + grndFlrCnt +
                    ", ugrndFlrCnt=" + ugrndFlrCnt +
                    ", indrAutoUtcnt=" + indrAutoUtcnt +
                    ", oudrAutoUtcnt=" + oudrAutoUtcnt +
                    ", indrMechUtcnt=" + indrMechUtcnt +
                    ", oudrMechUtcnt=" + oudrMechUtcnt +
                    ", stcnsDay='" + stcnsDay + '\'' +
                    ", useAprDay='" + useAprDay + '\'' +
                    ", newPlatPlc='" + newPlatPlc + '\'' +
                    ", platPlc='" + platPlc + '\'' +
                    ", rideUseElvtCnt=" + rideUseElvtCnt +
                    ", bldNm='" + bldNm + '\'' +
                    ", dongNm='" + dongNm + '\'' +
                    ", bun='" + bun + '\'' +
                    ", ji='" + ji + '\'' +
                    '}';
        }

        @JsonCreator
        public ItemDto(@JsonProperty("mainPurpsCdNm") String mainPurpsCdNm,
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
                    @JsonProperty("bun") String bun,
                    @JsonProperty("ji") String ji
                    ) {
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
            this.bun = bun;
            this.ji = ji;
        }
    }
}
