package com.memeki.reviewhome.geo.repository;

import com.memeki.reviewhome.geo.entity.GeoFeatures;
import java.util.List;

public interface GeoFeaturesRepositoryCustom {
    void saveFeature(GeoFeatures feature);
    List<GeoFeatures> findAllFGeoFeatures();
    List<GeoFeatures> findNearbyFeatures(double minLat, double minLng, double maxLat, double maxLng);
}