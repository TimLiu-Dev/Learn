public class Relax {
    public static void main(String[] args) {
        int INF = Integer.MAX_VALUE;
        int[] dist = {0, INF, INF, INF};               // source 0; nothing reaches 3
        int[][] edges = {{0, 1, 4}, {3, 2, 1}, {2, 1, 1}};   // {from, to, weight}
        for (int round = 0; round < 3; round++)        // Bellman-Ford: relax every edge V-1 times
            for (int[] e : edges)
                if (dist[e[0]] + e[2] < dist[e[1]]) dist[e[1]] = dist[e[0]] + e[2];
        System.out.println(dist[1]);
    }
}
