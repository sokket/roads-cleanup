package com.example.roadscleanup.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class VehiclesLocationApiResp {
    private Long id;
    private List<Double> pos;
}
