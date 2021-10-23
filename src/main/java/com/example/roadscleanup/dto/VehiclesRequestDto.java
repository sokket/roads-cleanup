package com.example.roadscleanup.dto;

import lombok.Data;

import java.util.List;

@Data
public class VehiclesRequestDto {
    private List<Features> features;
}