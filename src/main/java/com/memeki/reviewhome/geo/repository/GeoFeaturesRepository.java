package com.memeki.reviewhome.geo.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import com.memeki.reviewhome.geo.entity.GeoFeatures;

public interface GeoFeaturesRepository extends JpaRepository<GeoFeatures, Integer>  {
    GeoFeatures findByEmdCd(String emdCd);
    List<GeoFeatures> findAll();
}
