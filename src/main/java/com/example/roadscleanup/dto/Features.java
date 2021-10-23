package com.example.roadscleanup.dto;

import lombok.Data;

@Data
public class Features {
    private Geometry geometry;
    private VehiclesDto properties;
}
