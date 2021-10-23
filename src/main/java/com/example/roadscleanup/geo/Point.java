package com.example.roadscleanup.geo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Point {
    private double lon;
    private double lat;

    private long streetId;
}
