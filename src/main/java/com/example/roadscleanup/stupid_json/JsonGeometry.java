package com.example.roadscleanup.stupid_json;

import lombok.Data;

import java.util.List;

@Data
public class JsonGeometry {
    private List<List<Double>> coordinates;
}
