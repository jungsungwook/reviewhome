package com.memeki.reviewhome.geo.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.memeki.reviewhome.geo.entity.GeoFeatures;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class GeoFeaturesResponseDto {
    private int statusCode;
    private List<GeoFeatures> contents;
}
