package com.memeki.reviewhome.geo.utils.serializer;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.locationtech.jts.geom.Geometry;

import java.io.IOException;

public class GeometrySerializer extends JsonSerializer<Geometry> {
    @Override
    public void serialize(Geometry geometry, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeString(geometry.toText()); // 또는 다른 적절한 직렬화 방법
    }
}