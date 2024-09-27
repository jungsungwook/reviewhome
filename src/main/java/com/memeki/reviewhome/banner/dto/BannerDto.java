package com.memeki.reviewhome.banner.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.memeki.reviewhome.banner.entity.Banner;
import com.memeki.reviewhome.banner.entity.BannerContent;

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
@Schema(description = "배너 DTO")
public class BannerDto {
    @Schema(description = "배너 정보")
    private Banner banner;
    @Schema(description = "배너 컨텐츠 리스트")
    private List<BannerContent> bannerContentList;
}
