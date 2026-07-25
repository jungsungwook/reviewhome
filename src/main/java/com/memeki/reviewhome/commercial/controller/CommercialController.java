package com.memeki.reviewhome.commercial.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.memeki.reviewhome.commercial.dto.CommercialResponseDto;
import com.memeki.reviewhome.commercial.service.CommercialService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/commercial")
@Tag(name = "Commercial", description = "상권 정보 API")
public class CommercialController {
    
    @Autowired
    private CommercialService commercialService;
    
    @GetMapping("/building/{uuid}")
    @Operation(summary = "건물별 상권 정보 조회", description = "특정 건물의 주변 상권 정보를 조회합니다.")
    public ResponseEntity<CommercialResponseDto> getCommercialInfoByBuilding(
            @Parameter(description = "건물 정보 UUID", required = true)
            @PathVariable String uuid) {
        
        CommercialResponseDto response = commercialService.getCommercialInfoByUuid(uuid);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/category")
    @Operation(summary = "업종별 상권 정보 조회", description = "업종별로 상권 정보를 조회합니다.")
    public ResponseEntity<CommercialResponseDto> getCommercialInfoByCategory(
            @Parameter(description = "업종 코드", required = true, example = "R1")
            @RequestParam String categoryCode,
            @Parameter(description = "업종 분류 타입 (large, medium, small)", required = true, example = "large")
            @RequestParam String categoryType) {
        
        CommercialResponseDto response = commercialService.getCommercialInfoByCategory(categoryCode, categoryType);
        return ResponseEntity.ok(response);
    }
}
