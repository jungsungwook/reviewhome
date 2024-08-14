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
        geoFeaturesService.updateGeoFeatures();
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
        if(zoomLevel < 13){
            return ResponseEntity.badRequest().build();
        }
        List<GeoJson> features = geoFeaturesService.getNearbyFeatures(minLat, minLng, maxLat, maxLng, zoomLevel);
        GeoFeaturesNearByResponseDto responseDto = new GeoFeaturesNearByResponseDto();
        responseDto.setContents(features);
        return ResponseEntity.ok(responseDto);
    }

}
