package com.example.roadscleanup;

import com.example.roadscleanup.geo.Point;
import com.example.roadscleanup.stupid_json.JsonFeature;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.jgrapht.Graph;
import org.jgrapht.alg.interfaces.ShortestPathAlgorithm;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.BiConsumer;

@SpringBootApplication
public class RoadsCleanupApplication {

    private static double dst(Point dot1, Point dot2) {
        return Math.sqrt(Math.pow(dot1.getLat() - dot2.getLat(), 2.0) + Math.pow(dot1.getLon() - dot2.getLon(), 2.0));
    }

    public static void main(String[] args) {
        SpringApplication.run(RoadsCleanupApplication.class, args);

        try {
            var f = ResourceUtils.getFile(ResourceUtils.CLASSPATH_URL_PREFIX + "routes.json");
            var paths = Files.readString(f.toPath());
            Gson gson = new Gson();
            Type listType = new TypeToken<ArrayList<JsonFeature>>(){}.getType();
            List<JsonFeature> result = gson.fromJson(paths, listType);
            var dots = new ArrayList<Point>();
            var streetNames = new HashMap<Long, String>();
            Graph<Long, DefaultEdge> g = new DefaultDirectedGraph<>(DefaultEdge.class);
            HashMap<Long, List<Point>> dotsByStreetId = new HashMap<>();
            for (JsonFeature jsonFeature : result) {
                var streetId = jsonFeature.getProperties().getId();
                dotsByStreetId.put(streetId, new ArrayList<>());
                g.addVertex(streetId);
                streetNames.put(streetId, jsonFeature.getProperties().getName());
                for (List<Double> coordinates : jsonFeature.getGeometry().getCoordinates()) {
                    var d = new Point(coordinates.get(0), coordinates.get(1), streetId);
                    dots.add(d);
                    dotsByStreetId.get(streetId).add(d);
                }
            }
            //var out = new ArrayList<>();
            for (Point dot1 : dots) {
                for (Point dot2 : dots) {
                    if (dot1.getStreetId() == dot2.getStreetId()) {
                        continue;
                    }
                    if (Math.sqrt(Math.pow(dot1.getLat() - dot2.getLat(), 2.0) + Math.pow(dot1.getLon() - dot2.getLon(), 2.0)) <= 0.0001) {
                        System.out.println(streetNames.get(dot1.getStreetId()) + " -> " + streetNames.get(dot2.getStreetId()));
                        g.addEdge(dot1.getStreetId(), dot2.getStreetId());
                        g.addEdge(dot2.getStreetId(), dot1.getStreetId());
                    }
                }
            }

            DijkstraShortestPath<Long, DefaultEdge> dijkstraAlg = new DijkstraShortestPath<>(g);
            ShortestPathAlgorithm.SingleSourcePaths<Long, DefaultEdge> iPaths = dijkstraAlg.getPaths(16491L);
            double max = 0.0;
            long maxV = 16491L;
            for (Long aLong : streetNames.keySet()) {
                double h = iPaths.getWeight(aLong);
                if (h > max && h < 1000) {
                    max = h;
                    maxV = aLong;
                }
            }

            System.out.println(max);

            var out = new ArrayList<List<Point>>();
            for (Long aLong : iPaths.getPath(maxV).getVertexList()) {
                var ff = dotsByStreetId.get(aLong);
                var ffr = new ArrayList<Point>();
                for (Point point : ff) {
                    ffr.add(new Point(point.getLat(), point.getLon(), point.getStreetId()));
                }
                out.add(ffr);
            }
/*
            Point prevPoint = null;
            for (Long aLong : iPaths.getPath(maxV).getVertexList()) {
                if (prevPoint == null) {
                    prevPoint = dotsByStreetId.get(aLong).get(0);
                }
                var street = dotsByStreetId.get(aLong);
                if (dst(street.get(0), prevPoint) < dst(street.get(street.size() - 1), prevPoint)) {
                    Collections.reverse(street);
                }
                out.addAll(street);
                prevPoint = out.get(out.size() - 1);
            }*/

            /*Set<Point> added = new HashSet<>();
            ArrayList<Point> out2 = new ArrayList<>();
            HashMap<Long, Integer> visitedOnStreet = new HashMap<>();
            dotsByStreetId.forEach((id, vals) -> visitedOnStreet.put(id, vals.size()));
            for (Point point : out) {
                double minDst = 100000000.0;
                Point minDstP = null;
                if (visitedOnStreet.get(point.getStreetId()) >= 0) {
                    for (Point point1 : dotsByStreetId.get(point.getStreetId())) {
                        if (!added.contains(point) && point != point1 && dst(point1, point) < minDst) {
                            minDstP = point1;
                            minDst = dst(point1, point);
                        }
                    }
                } else {
                    for (Point point1 : out) {
                        if (!added.contains(point) && point != point1 && dst(point1, point) < minDst) {
                            minDstP = point1;
                            minDst = dst(point1, point);
                        }
                    }
                }
                if (minDstP == null && !added.contains(point)) {
                    //out2.add(point);
                    //added.add(point);
                    continue;
                }
                if (!added.contains(point) && point != null) {
                    out2.add(point);
                    added.add(point);
                    visitedOnStreet.put(point.getStreetId(), visitedOnStreet.get(point.getStreetId()) - 1);
                }
                if (!added.contains(minDstP) && minDstP != null) {
                    out2.add(minDstP);
                    added.add(minDstP);
                    visitedOnStreet.put(minDstP.getStreetId(), visitedOnStreet.get(minDstP.getStreetId()) - 1);
                }
            }

            ArrayList<Point> out3 = new ArrayList<>();
*/


            System.out.println(max);
            new File("./test.json").createNewFile();
            Files.writeString(Path.of("./test.json"), gson.toJson(out));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
