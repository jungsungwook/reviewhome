package com.memeki.reviewhome.geo.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString()
@JsonIgnoreProperties(ignoreUnknown = true)
public class GeoFeaturesByPostAddressInfoDto {
    private int id;
    private String emdCd;
    private String fullNm;
    private String emdKorNm;
}
