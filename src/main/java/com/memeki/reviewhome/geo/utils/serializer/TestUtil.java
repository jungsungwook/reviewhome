package com.memeki.reviewhome.geo.utils.serializer;

import java.io.IOException;

import org.locationtech.jts.geom.Geometry;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;

public class TestUtil {
    public void serialize(Geometry geometry, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeString(geometry.toText()); // 또는 다른 적절한 직렬화 방법
    }
}
