package com.example.roadscleanup;

import com.example.roadscleanup.geo.Point;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RoadsCleanupApplication {

    private static double dst(Point dot1, Point dot2) {
        return Math.sqrt(Math.pow(dot1.getLat() - dot2.getLat(), 2.0) + Math.pow(dot1.getLon() - dot2.getLon(), 2.0));
    }

    public static void main(String[] args) {
        SpringApplication.run(RoadsCleanupApplication.class, args);
    }

}
