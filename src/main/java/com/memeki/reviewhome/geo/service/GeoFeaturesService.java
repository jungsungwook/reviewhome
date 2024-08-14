package com.memeki.reviewhome.geo.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.WKTWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.memeki.reviewhome.geo.dto.GeoJson;
import com.memeki.reviewhome.geo.entity.GeoFeatures;
import com.memeki.reviewhome.geo.repository.GeoFeaturesRepository;
import com.memeki.reviewhome.geo.repository.GeoFeaturesRepositoryCustom;
import com.memeki.reviewhome.historyManager.entity.UpdateHistory;
import com.memeki.reviewhome.historyManager.repository.UpdateHistoryRepository;

import java.util.ArrayList;

@Service
public class GeoFeaturesService {
    @Autowired
    private GeoFeaturesRepository geoFeaturesRepository;

    @Autowired
    private UpdateHistoryRepository updateHistoryRepository;

    @Autowired
    private GeoFeaturesRepositoryCustom geoFeaturesRepositoryCustom;

    @Autowired
    private GeoJsonConverter geoJsonConverter;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${vworld-api-key}")
    private String apiKey;

    @Value("${domain}")
    private String domain;

    @Value("${vworld-api-url}")
    private String baseUrl;

    @Transactional
    public void updateGeoFeatures() {
        LocalDateTime lastUpdate = getLastUpdateTime();
        if (lastUpdate != null && lastUpdate.isAfter(LocalDateTime.now().minusMonths(1))) {
            return;
        }
        System.out.println("Updating geo features");
        List<JsonNode> allFeatures = getAllFeaturesApi();
        for (JsonNode featureNode : allFeatures) {
            GeoFeatures feature = convertToFeature(featureNode);
            GeoFeatures existingFeature = geoFeaturesRepository.findByEmdCd(feature.getEmdCd());
            if (existingFeature != null) {
                existingFeature.setFullNm(feature.getFullNm());
                existingFeature.setEmdKorNm(feature.getEmdKorNm());
                existingFeature.setGeometry(feature.getGeometry());
                geoFeaturesRepositoryCustom.saveFeature(existingFeature);
            } else {
                geoFeaturesRepositoryCustom.saveFeature(feature);
            }
        }

        // Update last update time
        UpdateHistory updateHistory = new UpdateHistory();
        updateHistory.setType("geoFeatures");
        updateHistory.setCreatedAt(LocalDateTime.now());
        updateHistoryRepository.save(updateHistory);
    }

    private LocalDateTime getLastUpdateTime() {
        List<UpdateHistory> histories = updateHistoryRepository.findByType("geoFeatures");
        if (histories.isEmpty()) {
            return null;
        }
        return histories.get(0).getCreatedAt();
    }

    private GeoFeatures convertToFeature(JsonNode featureNode) {
        GeoFeatures feature = new GeoFeatures();
        feature.setEmdCd(featureNode.path("properties").path("emd_cd").asText());
        feature.setFullNm(featureNode.path("properties").path("full_nm").asText());
        feature.setEmdKorNm(featureNode.path("properties").path("emd_kor_nm").asText());
        String geometryJson = featureNode.path("geometry").toString();
        Geometry geometry = geoJsonConverter.convertJsonToGeometry(geometryJson);

        feature.setGeometry(geometry);

        return feature;
    }

    public List<JsonNode> getAllFeaturesApi() {
        List<JsonNode> allFeatures = new ArrayList<>();
        int page = 1;
        boolean hasMorePages;

        do {
            // https://api.vworld.kr/req/data?service=data&version=2.0&request=GetFeature&format=json&errorformat=json&size=10&page=1&data=LT_C_ADEMD_INFO&geomfilter=BOX(124.60,
            // 33.11, 131.87,
            // 38.61)&columns=emd_cd,full_nm,emd_kor_nm,ag_geom&geometry=true&attribute=true&key=7913502E-4DAA-35F7-82E7-8144254733F3&domain=localhost
            String url = String.format(
                    "%s?size=50&columns=emd_cd,full_nm,emd_kor_nm,ag_geom&geometry=true&attribute=true&data=LT_C_ADEMD_INFO&format=json&errorformat=json&service=data&request=GetFeature&geomfilter=BOX(124.60,33.11,131.87,38.61)&page=%d&key=%s&domain=%s",
                    baseUrl, page, apiKey, domain);
            System.out.println(url);
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            JsonNode responseJson;
            try {
                responseJson = objectMapper.readTree(response.getBody());
            } catch (Exception e) {
                throw new RuntimeException("Failed to parse response", e);
            }

            JsonNode features = responseJson.path("response").path("result").path("featureCollection").path("features");
            if (features.isArray()) {
                for (JsonNode feature : features) {
                    allFeatures.add(feature);
                }
            }

            int totalRecords = responseJson.path("response").path("record").path("total").asInt();
            int currentPageRecords = responseJson.path("response").path("record").path("current").asInt();
            int pageSize = responseJson.path("response").path("page").path("size").asInt();

            hasMorePages = (page * pageSize) < totalRecords;
            page++;

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } while (hasMorePages);

        return allFeatures;
    }

    public List<GeoFeatures> getAllFeatures() {
        List<GeoFeatures> features = geoFeaturesRepositoryCustom.findAllFGeoFeatures();
        return features;
    }

    public List<GeoJson> getNearbyFeatures(double minLat, double minLng, double maxLat, double maxLng,
            int zoomLevel) {
        List<GeoFeatures> find = geoFeaturesRepositoryCustom.findNearbyFeatures(minLat, minLng, maxLat, maxLng);
        return find.stream()
                .map(GeoJsonConverter::convertToGeoJSON)
                .collect(Collectors.toList());
    }
}
