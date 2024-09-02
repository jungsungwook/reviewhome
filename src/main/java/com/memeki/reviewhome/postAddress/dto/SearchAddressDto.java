package com.memeki.reviewhome.postAddress.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class SearchAddressDto {
    @Builder
    @Getter
    @NoArgsConstructor
    public static class Request {
        private String sigunguCd;
        private String bjdongCd;
        private String bun;
        private String ji;
        private String dongNm;
        private String newPlatPlc;

        @JsonCreator
        public Request(
                @JsonProperty("sigunguCd") String sigunguCd,
                @JsonProperty("bjdongCd") String bjdongCd,
                @JsonProperty("bun") String bun,
                @JsonProperty("ji") String ji,
                @JsonProperty("dongNm") String dongNm,
                @JsonProperty("newPlatPlc") String newPlatPlc
                ) {
            this.sigunguCd = sigunguCd;
            this.bjdongCd = bjdongCd;
            this.bun = adjustBunValue(bun);
            this.ji = adjustJiValue(ji);
            this.dongNm = dongNm;
            this.newPlatPlc = newPlatPlc;
        }

        private String adjustBunValue(String bun) {
            // bun이 null이거나 이미 4자리인 경우 그대로 반환
            if (bun == null || bun.length() == 4) {
                return bun;
            }
            // 4자리로 만들기 위해 앞에 0을 추가하여 반환
            return String.format("%04d", Integer.parseInt(bun));
        }

        private String adjustJiValue(String ji) {
            // ji가 null이거나 이미 4자리인 경우 그대로 반환
            if (ji == null || ji.length() == 4) {
                return ji;
            }
            // 4자리로 만들기 위해 앞에 0을 추가하여 반환
            return String.format("%04d", Integer.parseInt(ji));
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class Response {
        private int statusCode;
        private GetBrTitleInfoResponseDto.ItemDto item;
        private String point_x;
        private String point_y;
        private List<String> dongNm;
    }
}
