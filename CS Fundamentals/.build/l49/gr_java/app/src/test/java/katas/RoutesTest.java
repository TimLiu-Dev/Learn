package katas;

import static katas.Routes.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.Test;

class RoutesTest {
    private final Graph tiny = parseGraph(App.resource("tinyEWD.txt"));

    @Test void routeToYourselfIsFree() {
        assertEquals(new Route(0.0, List.of(3)), shortestPath(tiny, 3, 3).orElseThrow());
    }

    @Test void unreachableVertexGivesEmpty() {
        assertTrue(shortestPath(parseGraph("3\n1\n0 1 0.5"), 0, 2).isEmpty());
    }

    @Test void tinyEwd0To6Is151Via273() {
        Route r = shortestPath(tiny, 0, 6).orElseThrow();
        assertEquals(1.51, r.distance(), 1e-9);              // doubles: compare with a tolerance (L2)
        assertEquals(List.of(0, 2, 7, 3, 6), r.vertices());
    }

    @Test void everyStepIsARealEdgeAndTheWeightsAddUp() {
        for (int to = 0; to < tiny.vertexCount(); to++) {
            Route r = shortestPath(tiny, 0, to).orElseThrow();
            double total = 0;
            for (int i = 0; i + 1 < r.vertices().size(); i++) {
                int a = r.vertices().get(i), b = r.vertices().get(i + 1);
                total += tiny.edges().stream().filter(e -> e.from() == a && e.to() == b)
                    .mapToDouble(Edge::weight).min().orElseThrow();   // throws if no such edge
            }
            assertEquals(r.distance(), total, 1e-9);
        }
    }

    @Test void negativeWeightIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> shortestPath(parseGraph("2\n1\n0 1 -0.5"), 0, 1));
    }

    @Test void agreesWithBellmanFordOnEveryVertexOfMediumEwd() {
        Graph medium = parseGraph(App.resource("mediumEWD.txt"));
        double[] oracle = bellmanFord(medium, 0);
        for (int to = 0; to < medium.vertexCount(); to++) {
            var r = shortestPath(medium, 0, to);
            if (oracle[to] == Double.POSITIVE_INFINITY) assertTrue(r.isEmpty());
            else assertEquals(oracle[to], r.orElseThrow().distance(), 1e-9, "vertex " + to);
        }
    }

    @Test void cheapestNetworkForTinyEwgHas7EdgesWeighing181() {
        List<Edge> net = cheapestNetwork(parseGraph(App.resource("tinyEWG.txt"))).orElseThrow();
        assertEquals(7, net.size());
        assertEquals(1.81, net.stream().mapToDouble(Edge::weight).sum(), 1e-9);
    }

    @Test void disconnectedGraphHasNoSpanningTree() {
        assertTrue(cheapestNetwork(parseGraph("4\n2\n0 1 1.0\n2 3 1.0")).isEmpty());
        assertTrue(cheapestNetwork(parseGraph("1\n0")).isPresent());   // one vertex: an empty tree
    }
}
