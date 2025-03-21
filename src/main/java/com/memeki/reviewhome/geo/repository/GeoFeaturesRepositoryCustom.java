package com.memeki.reviewhome.geo.repository;

import com.memeki.reviewhome.geo.dto.GeoFeaturesByPostAddressInfoDto;
import com.memeki.reviewhome.geo.entity.GeoFeatures;
import java.util.List;

public interface GeoFeaturesRepositoryCustom {
    void saveFeature(GeoFeatures feature);
    GeoFeaturesByPostAddressInfoDto findByEmdCd(String emdCd);
    List<GeoFeatures> findAllFGeoFeatures();
    List<GeoFeatures> findNearbyFeatures(double minLat, double minLng, double maxLat, double maxLng);
}