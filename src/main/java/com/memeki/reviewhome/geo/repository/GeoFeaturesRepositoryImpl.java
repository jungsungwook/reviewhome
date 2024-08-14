package com.memeki.reviewhome.geo.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import com.memeki.reviewhome.geo.entity.GeoFeatures;
import com.memeki.reviewhome.geo.service.GeoJsonConverter;
import java.util.List;

@Repository
public class GeoFeaturesRepositoryImpl implements GeoFeaturesRepositoryCustom {
    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final GeoJsonConverter geoJsonConverter;

    @Autowired
    public GeoFeaturesRepositoryImpl(
            JdbcTemplate jdbcTemplate,
            GeoJsonConverter geoJsonConverter,
            NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.geoJsonConverter = geoJsonConverter;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    @Override
    public void saveFeature(GeoFeatures feature) {
        // Geometry 객체를 WKT 문자열로 변환
        String wkt = geoJsonConverter.convertGeometryToWKT(feature.getGeometry());

        // SQL 쿼리 생성
        String sql = "INSERT INTO geo_features (emd_cd, emd_kor_nm, full_nm, geometry) VALUES (?, ?, ?, ST_GeomFromText(?))";

        // SQL 쿼리 실행
        jdbcTemplate.update(sql, feature.getEmdCd(), feature.getEmdKorNm(), feature.getFullNm(), wkt);
    }

    @Override
    public List<GeoFeatures> findAllFGeoFeatures() {
        // SQL 쿼리 생성
        String sql = "SELECT id, emd_cd, emd_kor_nm, full_nm, ST_AsText(geometry) FROM geo_features";

        // SQL 쿼리 실행
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            GeoFeatures feature = new GeoFeatures();
            feature.setId(rs.getInt("id"));
            feature.setEmdCd(rs.getString("emd_cd"));
            feature.setEmdKorNm(rs.getString("emd_kor_nm"));
            feature.setFullNm(rs.getString("full_nm"));
            feature.setGeometry(geoJsonConverter.convertWKTToGeometry(rs.getString("ST_AsText(geometry)")));
            return feature;
        });
    }

    public List<GeoFeatures> findNearbyFeatures(double minLat, double minLng, double maxLat, double maxLng) {
        String sql = "SELECT id, emd_cd, emd_kor_nm, full_nm, ST_AsText(geometry) " +
                "FROM geo_features " +
                "WHERE ST_Intersects(geometry, ST_GeomFromText(:bbox))";
        
        String bbox = String.format("POLYGON((%f %f, %f %f, %f %f, %f %f, %f %f))",
                minLng, minLat, maxLng, minLat, maxLng, maxLat, minLng, maxLat, minLng, minLat);

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("bbox", bbox);
        return namedParameterJdbcTemplate.query(sql, params, geoFeaturesRowMapper());
    }

    private RowMapper<GeoFeatures> geoFeaturesRowMapper() {
        return (rs, rowNum) -> {
            GeoFeatures feature = new GeoFeatures();
            feature.setId(rs.getInt("id"));
            feature.setEmdCd(rs.getString("emd_cd"));
            feature.setEmdKorNm(rs.getString("emd_kor_nm"));
            feature.setFullNm(rs.getString("full_nm"));
            feature.setGeometry(geoJsonConverter.convertWKTToGeometry(rs.getString("ST_AsText(geometry)")));
            return feature;
        };
    }
}
