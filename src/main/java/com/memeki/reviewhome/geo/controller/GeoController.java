package com.memeki.reviewhome.geo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.memeki.reviewhome.geo.dto.GeoFeaturesNearByResponseDto;
import com.memeki.reviewhome.geo.dto.GeoFeaturesResponseDto;
import com.memeki.reviewhome.geo.dto.GeoJson;
import com.memeki.reviewhome.geo.entity.GeoFeatures;
import com.memeki.reviewhome.geo.service.GeoFeaturesService;
import com.memeki.reviewhome.geo.service.GeoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import java.util.List;

@RestController
@RequestMapping(value = "/api/geo")
public class GeoController {
    @Autowired
    private GeoService geoService;

    @Autowired
    private GeoFeaturesService geoFeaturesService;

    @GetMapping("/features")
    public ResponseEntity<GeoFeaturesResponseDto> getFeatures() throws Exception {
        GeoFeaturesResponseDto responseDto = new GeoFeaturesResponseDto();
        responseDto.setContents(geoFeaturesService.getAllFeatures());
        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/features/nearby")
    public ResponseEntity<GeoFeaturesNearByResponseDto> getNearbyFeatures(
            @RequestParam("minLat") double minLat,
            @RequestParam("minLng") double minLng,
            @RequestParam("maxLat") double maxLat,
            @RequestParam("maxLng") double maxLng,
            @RequestParam("zoom") int zoomLevel // 줌 레벨 파라미터 추가
    ) {
        if (zoomLevel < 13) {
            return ResponseEntity.badRequest().build();
        }
        List<GeoJson> features = geoFeaturesService.getNearbyFeatures(minLat, minLng, maxLat, maxLng, zoomLevel);
        GeoFeaturesNearByResponseDto responseDto = new GeoFeaturesNearByResponseDto();
        responseDto.setContents(features);
        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/bjdong")
    @Operation(summary = "법정동 조회", description = "법정동을 조회합니다.", parameters = {
            @Parameter(name = "bjdongCd", description = "법정동 코드", example = "1168010300"),
            @Parameter(name = "bjdongNm", description = "법정동 이름", example = "신사동"),
            @Parameter(name = "bjdongType", description = "법정동 타입", example = "1")
    })
    public ResponseEntity<GeoJson> getBjdong(
            @RequestParam(required = false) String bjdongCd,
            @RequestParam(required = false) String bjdongNm,
            @RequestParam(required = false) Integer bjdongType) {
        geoService.getBjdong(bjdongCd, bjdongNm, bjdongType);
        return ResponseEntity.ok(null);
    }

}
