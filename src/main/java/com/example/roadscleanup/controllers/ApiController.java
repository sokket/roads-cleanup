package com.example.roadscleanup.controllers;

import com.example.roadscleanup.dto.VehiclesApiResp;
import com.example.roadscleanup.dto.VehiclesLocationApiResp;
import com.example.roadscleanup.services.ApiService;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/")
public class ApiController {

    private final ApiService routsService;

    public ApiController(ApiService routsService) {
        this.routsService = routsService;
    }

    @GetMapping("vehicles")
    public ResponseEntity<Set<VehiclesApiResp>> getVehicles() {
        return ResponseEntity
                .ok()
                .body(routsService.getVehiclesList());
    }

    @GetMapping("locations")
    public ResponseEntity<Set<VehiclesLocationApiResp>> getVehiclesLocation() {
        return ResponseEntity
                .ok()
                .body(routsService.getVehiclesLocation());
    }

    @GetMapping("streets")
    public ResponseEntity<String> getStreetData() {
        try {
            File file = ResourceUtils.getFile(ResourceUtils.CLASSPATH_URL_PREFIX + "routes.json");
            return ResponseEntity
                    .ok()
                    .body(Files.readString(file.toPath()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ResponseEntity.internalServerError().build();
    }
}
