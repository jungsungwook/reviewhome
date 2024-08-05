package com.memeki.reviewhome.geo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.memeki.reviewhome.geo.entity.GeoLocation;

public interface GeoLocationRepository extends JpaRepository<GeoLocation, Integer> {
    GeoLocation findGeoLocationById(int id);

    GeoLocation findGeoLocationByPostAddressInfoUuid(String postAddressInfoUuid);
}
