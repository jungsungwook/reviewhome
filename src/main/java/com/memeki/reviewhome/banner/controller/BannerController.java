package com.memeki.reviewhome.banner.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.memeki.reviewhome.banner.dto.BannerResponseDto;
import com.memeki.reviewhome.banner.service.BannerService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@Tag(name = "BannerController", description = "배너 관련 API")
@RequestMapping(value = "/api/banner")
public class BannerController {
    @Autowired
    private BannerService bannerService;

    @GetMapping("")
    @Operation(summary = "배너 조회", description = "배너를 조회합니다.", parameters = {
            @Parameter(name = "id", description = "배너 ID", example = "1"),
            @Parameter(name = "type", description = "배너 타입", example = "promotional"),
            @Parameter(name = "route", description = "배너를 사용하는 경로", example = "/home"),
            @Parameter(name = "name", description = "배너 이름", example = "Summer Sale")
    })
    public ResponseEntity<BannerResponseDto> getBanner(
            @RequestParam(required = false) Long id,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String route,
            @RequestParam(required = false) String name) {
        BannerResponseDto bannerResponseDto = new BannerResponseDto();
        bannerResponseDto.setStatusCode(200);
        bannerResponseDto.setBannerDto(bannerService.searchBanners(id, type, route, name).getBannerDto());
        return ResponseEntity.ok(bannerResponseDto);
    }

}
