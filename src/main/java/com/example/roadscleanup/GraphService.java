package com.example.roadscleanup;

import com.example.roadscleanup.dto.RouteResp;
import com.example.roadscleanup.geo.Point;
import com.example.roadscleanup.stupid_json.JsonFeature;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.jgrapht.Graph;
import org.jgrapht.alg.interfaces.ShortestPathAlgorithm;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.AbstractBaseGraph;
import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import javax.annotation.PostConstruct;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

@Service
public class GraphService {

    private final Graph<Long, DefaultEdge> g = new DefaultDirectedWeightedGraph<>(DefaultEdge.class);
    private final HashMap<Long, List<Point>> dotsByStreetId = new HashMap<>();

    private Graph<Long, DefaultEdge> getGraph() {
        AbstractBaseGraph<Long, DefaultEdge> f = (AbstractBaseGraph<Long, DefaultEdge>) g;
        return (Graph<Long, DefaultEdge>) f.clone();
    }

    @PostConstruct
    void init() {
        try {
            var f = ResourceUtils.getFile(ResourceUtils.CLASSPATH_URL_PREFIX + "routes.json");
            var paths = Files.readString(f.toPath());
            Gson gson = new Gson();
            Type listType = new TypeToken<ArrayList<JsonFeature>>() {}.getType();
            List<JsonFeature> result = gson.fromJson(paths, listType);
            var dots = new ArrayList<Point>();
            var streetNames = new HashMap<Long, String>();

            for (JsonFeature jsonFeature : result) {
                var streetId = jsonFeature.getProperties().getId();
                dotsByStreetId.put(streetId, new ArrayList<>());
                streetNames.put(streetId, jsonFeature.getProperties().getName());
                for (List<Double> coordinates : jsonFeature.getGeometry().getCoordinates()) {
                    var d = new Point(coordinates.get(0), coordinates.get(1), streetId);
                    dots.add(d);
                    dotsByStreetId.get(streetId).add(d);
                }
            }

            for (Long aLong : dotsByStreetId.keySet()) {
                g.addVertex(aLong);
            }
            for (Point dot1 : dots) {
                for (Point dot2 : dots) {
                    if (dot1.getStreetId() == dot2.getStreetId()) {
                        continue;
                    }
                    if (Math.sqrt(Math.pow(dot1.getLat() - dot2.getLat(), 2.0) + Math.pow(dot1.getLon() - dot2.getLon(), 2.0)) <= 0.0005) {
                        System.out.println(streetNames.get(dot1.getStreetId()) + " -> " + streetNames.get(dot2.getStreetId()));
                        g.addEdge(dot1.getStreetId(), dot2.getStreetId());
                        g.addEdge(dot2.getStreetId(), dot1.getStreetId());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<RouteResp> getRoutes(int count) {
        var g = getGraph();
        var resp = new ArrayList<RouteResp>();
        for (int i = 0; i < count; i++) {
            DijkstraShortestPath<Long, DefaultEdge> dijkstraAlg = new DijkstraShortestPath<>(g);
            ShortestPathAlgorithm.SingleSourcePaths<Long, DefaultEdge> iPaths = dijkstraAlg.getPaths(16491L);
            double max = 0.0;
            long maxV = 16491L;
            for (Long aLong : dotsByStreetId.keySet()) {
                double h = iPaths.getWeight(aLong);
                if (h > max && h < 1000) {
                    max = h;
                    maxV = aLong;
                }
            }

            System.out.println(max);
            var out = new ArrayList<List<List<Double>>>();
            for (Long aLong : iPaths.getPath(maxV).getVertexList()) {
                for (DefaultEdge defaultEdge : g.edgesOf(aLong)) {
                    if (Objects.equals(g.getEdgeTarget(defaultEdge), aLong)) {
                        g.setEdgeWeight(defaultEdge, g.getEdgeWeight(defaultEdge) / 10);
                    }
                }
                var ff = dotsByStreetId.get(aLong);
                var ffr = new ArrayList<List<Double>>();
                for (Point point : ff) {
                    ffr.add(List.of(point.getLon(), point.getLat()));
                }
                out.add(ffr);
            }
            resp.add(new RouteResp(i, out));
        }
        return resp;
    }

}
