package com.memeki.reviewhome.geo.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class GeoFeaturesNearByResponseDto {
    private int statusCode;
    private List<GeoJson> contents;
}
