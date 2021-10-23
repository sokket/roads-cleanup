package com.example.roadscleanup.controllers;

import com.example.roadscleanup.dto.VehiclesApiResp;
import com.example.roadscleanup.dto.VehiclesLocationApiResp;
import com.example.roadscleanup.services.ApiService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@RequestMapping("/api/v1/")
public class ApiController {

    private final ApiService routsService;

    public ApiController(ApiService routsService) {
        this.routsService = routsService;
    }

    @GetMapping("vehicles")
    public Set<VehiclesApiResp> getVehicles() {
        return routsService.getVehiclesList();
    }

    @GetMapping("locations")
    public Set<VehiclesLocationApiResp> getVehiclesLocation() {
        return routsService.getVehiclesLocation();
    }

}
