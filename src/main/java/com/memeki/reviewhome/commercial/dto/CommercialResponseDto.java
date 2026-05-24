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
@Schema(description = "상권 정보 응답 DTO")
public class CommercialResponseDto {
    
    @Schema(description = "상태 코드", example = "200")
    private int statusCode;
    
    @Schema(description = "메시지", example = "성공")
    private String message;
    
    @Schema(description = "상권 정보 목록")
    private List<CommercialItemDto> commercialList;
    
    @Schema(description = "총 개수", example = "15")
    private int totalCount;
}
