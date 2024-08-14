package com.memeki.reviewhome.geo.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class GeoJson {
    @JsonProperty("type")
    private final String type = "Feature";

    @JsonProperty("geometry")
    private Map<String, Object> geometry;

    @JsonProperty("properties")
    private Map<String, Object> properties;

    // Getters and Setters
    public String getType() {
        return type;
    }

    public Map<String, Object> getGeometry() {
        return geometry;
    }

    public void setGeometry(Map<String, Object> geometry) {
        this.geometry = geometry;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }
}
