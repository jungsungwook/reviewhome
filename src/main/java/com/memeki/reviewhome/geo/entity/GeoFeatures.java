package com.memeki.reviewhome.geo.entity;

import javax.persistence.*;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import org.geolatte.geom.GeometryType;
import org.hibernate.annotations.TypeDef;
import org.locationtech.jts.geom.Geometry;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.memeki.reviewhome.geo.utils.serializer.GeometrySerializer;

@Entity
@Table(name = "geo_features")
@TypeDef(name = "geometry", typeClass = GeometryType.class)
@Getter
@Setter
@ToString()
@NoArgsConstructor
public class GeoFeatures {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "emd_cd", nullable = false, unique = true)
    private String emdCd;

    @Column(name = "full_nm", nullable = false)
    private String fullNm;

    @Column(name = "emd_kor_nm", nullable = false)
    private String emdKorNm;

    @Column(name = "geometry", columnDefinition = "GEOMETRY")
    @JsonSerialize(using = GeometrySerializer.class)
    private Geometry geometry;
}
