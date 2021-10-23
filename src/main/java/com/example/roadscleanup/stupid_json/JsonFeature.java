package com.example.roadscleanup.stupid_json;

import lombok.Data;

@Data
public class JsonFeature {
    private JsonGeometry geometry;
    private JsonProperties properties;
}
