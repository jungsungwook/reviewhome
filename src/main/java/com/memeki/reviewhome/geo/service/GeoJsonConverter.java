package com.memeki.reviewhome.geo.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.memeki.reviewhome.geo.dto.GeoJson;
import com.memeki.reviewhome.geo.entity.GeoFeatures;

import org.geotools.geojson.GeoJSON;
import org.geotools.geojson.geom.GeometryJSON;
import org.locationtech.jts.geom.Geometry;

import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;
import org.locationtech.jts.io.WKTWriter;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

@Service
public class GeoJsonConverter {

    public Geometry convertJsonToGeometry(String geometryJson) {
        GeometryJSON gjson = new GeometryJSON();
        try (StringReader reader = new StringReader(geometryJson)) {
            return gjson.read(reader);
        } catch (IOException e) {
            throw new RuntimeException("Failed to convert GeoJSON to Geometry", e);
        }
    }

    public String convertGeometryToWKT(Geometry geometry) {
        WKTWriter writer = new WKTWriter();
        return writer.write(geometry);
    }

    // convertWKTToGeometry 메소드 추가
    public Geometry convertWKTToGeometry(String wkt) {
        WKTReader reader = new WKTReader();
        try {
            Geometry geometry = reader.read(wkt);
            return geometry;
        } catch (ParseException e) {
            throw new RuntimeException("Failed to convert WKT to Geometry", e);
        }
    }

    @SuppressWarnings("deprecation")
    public static GeoJson convertToGeoJSON(GeoFeatures geoFeatures) {
        GeoJson geoJson = new GeoJson();

        // GeoTools를 사용하여 Geometry를 GeoJSON으로 변환
        Geometry geometry = geoFeatures.getGeometry();
        GeometryJSON geometryJSON = new GeometryJSON();
        StringWriter writer = new StringWriter();
        try {
            geometryJSON.write(geometry, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String geometryGeoJson = writer.toString();

        // GeoJSON 문자열을 Map으로 변환
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> geometryMap = new HashMap<>();
        try {
            geometryMap = objectMapper.readValue(geometryGeoJson, Map.class);
        } catch (IOException e) {
            e.printStackTrace(); // 예외 처리: 로그에 예외를 기록합니다.
        }
        geoJson.setGeometry(geometryMap);

        // 속성 설정
        Map<String, Object> properties = new HashMap<>();
        properties.put("emdCd", geoFeatures.getEmdCd());
        properties.put("emdKorNm", geoFeatures.getEmdKorNm());
        properties.put("fullNm", geoFeatures.getFullNm());
        geoJson.setProperties(properties);

        return geoJson;
    }
}
