package com.example.roadscleanup.services;

import com.example.roadscleanup.dto.Features;
import com.example.roadscleanup.dto.VehiclesApiResp;
import com.example.roadscleanup.dto.VehiclesLocationApiResp;
import com.example.roadscleanup.dto.VehiclesRequestDto;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.lang3.StringEscapeUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ApiService {

    private VehiclesRequestDto getData() {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://gis-api.admlr.lipetsk.ru/api/v1/roads/egts/getpoints")
                .get()
                .build();

        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();
        try {
            Response response = client.newCall(request).execute();
            String str = Objects.requireNonNull(response.body()).string();
            str = cleanup(StringEscapeUtils.unescapeJava(str));

            System.out.println(str);

            return gson.fromJson(str,
                    VehiclesRequestDto.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Set<VehiclesApiResp> getVehiclesList() {
        var cars = getData();

        if (cars == null) return Collections.emptySet();

        return cars.getFeatures().stream()
                .map(Features::getProperties)
                .peek(it -> it.setName(cleanup(StringEscapeUtils.unescapeJava(it.getName()))))
                .map(it -> new VehiclesApiResp(it.getUid(), it.getName()))
                .collect(Collectors.toSet());
    }

    public Set<VehiclesLocationApiResp> getVehiclesLocation() {

        var cars = getData();

        if (cars == null) return Collections.emptySet();

        return cars.getFeatures().stream()
                .map(it -> new VehiclesLocationApiResp(it.getProperties().getUid(), it.getGeometry().getCoordinates()))
                .collect(Collectors.toSet());
    }


    private String cleanup(String str) {
        return str.substring(1, str.length() - 1);
    }
}
