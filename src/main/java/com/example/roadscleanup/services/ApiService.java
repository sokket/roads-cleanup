package com.example.roadscleanup.services;

import com.example.roadscleanup.dto.Features;
import com.example.roadscleanup.dto.VehiclesApiResp;
import com.example.roadscleanup.dto.VehiclesDto;
import com.example.roadscleanup.dto.VehiclesRequestDto;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.Reader;
import java.lang.reflect.Type;
import com.google.gson.reflect.TypeToken;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.lang3.StringEscapeUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ApiService {

    public Set<VehiclesApiResp> getVehiclesList() {
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
            var cars = gson.fromJson(str,
                    VehiclesRequestDto.class);

            return cars.getFeatures().stream()
                    .map(Features::getProperties)
                    .peek(it -> it.setName(cleanup(StringEscapeUtils.unescapeJava(it.getName()))))
                    .map(it -> new VehiclesApiResp(it.getUid(), it.getName()))
                    .collect(Collectors.toSet());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Collections.emptySet();
    }

    private String cleanup(String str) {
        return str.substring(1, str.length() - 1);
    }
}
