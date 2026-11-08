package katas;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class App {
    static String resource(String name) {
        try (InputStream in = Objects.requireNonNull(App.class.getResourceAsStream("/" + name), "missing " + name)) {
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public static void main(String[] args) {
        Routes.Graph roads = Routes.parseGraph(resource("tinyEWD.txt"));
        for (int to = 0; to < roads.vertexCount(); to++) {
            final int t = to;
            System.out.println(Routes.shortestPath(roads, 0, to)
                .map(r -> String.format("0 to %d (%.2f)  %s", t, r.distance(),
                    r.vertices().stream().map(String::valueOf).collect(Collectors.joining("->"))))
                .orElse("0 to " + to + ": unreachable"));
        }
        List<Routes.Edge> net = Routes.cheapestNetwork(Routes.parseGraph(resource("tinyEWG.txt"))).orElseThrow();
        System.out.println(String.format("cheapest network: %d edges, weight %.2f",
            net.size(), net.stream().mapToDouble(Routes.Edge::weight).sum()));
    }
}
