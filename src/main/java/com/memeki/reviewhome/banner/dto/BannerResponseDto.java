package com.memeki.reviewhome.banner.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@ToString
@Schema(description = "배너 응답 DTO")
public class BannerResponseDto {
    @Schema(description = "상태 코드", example = "200")
    private int statusCode;
    @Schema(description = "배너 DTO 리스트")
    private List<BannerDto> bannerDto;
}
