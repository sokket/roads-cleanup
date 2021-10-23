package com.example.roadscleanup.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class RouteResp {
    private long id;
    private List<List<List<Double>>> streets;
}
