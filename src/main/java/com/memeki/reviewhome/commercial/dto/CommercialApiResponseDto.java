package com.memeki.reviewhome.commercial.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@Schema(description = "상권 API 응답 DTO")
public class CommercialApiResponseDto {
    
    @Schema(description = "응답 헤더")
    private Header header;
    
    @Schema(description = "응답 본문")
    private Body body;
    
    @Getter
    @Setter
    @ToString
    @NoArgsConstructor
    @Schema(description = "응답 헤더")
    public static class Header {
        @Schema(description = "설명", example = "소상공인시장진흥공단 반경내 상가업소정보")
        private String description;
        
        @Schema(description = "컬럼 정보")
        private List<String> columns;
        
        @Schema(description = "기준년월", example = "202506")
        private String stdrYm;
        
        @Schema(description = "결과 코드", example = "00")
        private String resultCode;
        
        @Schema(description = "결과 메시지", example = "NORMAL SERVICE")
        private String resultMsg;
    }
    
    @Getter
    @Setter
    @ToString
    @NoArgsConstructor
    @Schema(description = "응답 본문")
    public static class Body {
        @Schema(description = "상권 업소 목록")
        private List<Object> items;
    }
}
