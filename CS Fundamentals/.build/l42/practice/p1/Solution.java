import java.util.*;

public class Practice {
    static class Backend {
        final String name;
        boolean healthy = true;
        int active = 0;     // requests in flight right now
        int served = 0;     // requests sent here so far
        Backend(String name) { this.name = name; }
    }

    enum Strategy { ROUND_ROBIN, LEAST_CONNECTIONS }

    static class Balancer {
        private final List<Backend> backends;
        private final Strategy strategy;
        private int next = 0;

        Balancer(List<Backend> backends, Strategy strategy) { this.backends = backends; this.strategy = strategy; }

        // Choose a healthy backend, count the request on it, and return it (null if none is healthy).
        Backend pick() {
            List<Backend> up = backends.stream().filter(b -> b.healthy).toList();
            if (up.isEmpty()) return null;
            Backend b = switch (strategy) {
                case ROUND_ROBIN -> up.get(Math.floorMod(next++, up.size()));
                case LEAST_CONNECTIONS -> up.stream().min(Comparator.comparingInt(x -> x.active)).orElseThrow();
            };
            b.active++;         // least-connections only works if picks are counted
            b.served++;
            return b;
        }

        // A request on b has finished.
        void finish(Backend b) { b.active--; }
    }

    // CHECKS
    public static void main(String[] args) {
        check("round-robin takes each backend in turn", "b1 b2 b3 b1", () -> names(new Balancer(three(), Strategy.ROUND_ROBIN), 4));
        check("round-robin skips an unhealthy backend", "b1 b3 b1 b3", () -> {
            List<Backend> bs = three();
            bs.get(1).healthy = false;
            return names(new Balancer(bs, Strategy.ROUND_ROBIN), 4);
        });
        check("a pick counts the request, and finish ends it", "active 1, then 0, served 1", () -> {
            Balancer lb = new Balancer(three(), Strategy.ROUND_ROBIN);
            Backend b = lb.pick();
            int during = b.active;
            lb.finish(b);
            return "active " + during + ", then " + b.active + ", served " + b.served;
        });
        check("least-connections picks the fewest in flight, ties to the earliest", "b1 b2 b3 | b1 b3 b1", () -> {
            List<Backend> bs = three();
            Balancer lb = new Balancer(bs, Strategy.LEAST_CONNECTIONS);
            String first = names(lb, 3);
            lb.finish(bs.get(0));
            lb.finish(bs.get(2));            // now b1=0, b2=1, b3=0 in flight
            return first + " | " + names(lb, 3);
        });
        check("no healthy backend gives null", null, () -> {
            List<Backend> bs = three();
            bs.forEach(b -> b.healthy = false);
            return new Balancer(bs, Strategy.LEAST_CONNECTIONS).pick();
        });
        check("round-robin with one slow backend", "201 | b1=10 b2=10 b3=10", () -> simulate(Strategy.ROUND_ROBIN));
        check("least-connections with one slow backend", "41 | b1=15 b2=2 b3=13", () -> simulate(Strategy.LEAST_CONNECTIONS));
    }

    static List<Backend> three() { return List.of(new Backend("b1"), new Backend("b2"), new Backend("b3")); }

    static String names(Balancer lb, int n) {
        StringJoiner out = new StringJoiner(" ");
        for (int i = 0; i < n; i++) out.add(lb.pick().name);
        return out.toString();
    }

    // One request arrives per tick. Each backend serves one request at a time;
    // b2 takes 20 ticks, b1 and b3 take 2. Returns "last finish tick | served".
    static String simulate(Strategy strategy) {
        List<Backend> bs = three();
        Map<String, Integer> cost = Map.of("b1", 2, "b2", 20, "b3", 2);
        Map<String, Integer> busyUntil = new HashMap<>(Map.of("b1", 0, "b2", 0, "b3", 0));
        Balancer lb = new Balancer(bs, strategy);
        List<Object[]> running = new ArrayList<>();          // {finish tick, backend}
        int last = 0;
        for (int t = 0; t < 30; t++) {
            for (Iterator<Object[]> it = running.iterator(); it.hasNext(); ) {
                Object[] r = it.next();
                if ((int) r[0] <= t) { lb.finish((Backend) r[1]); it.remove(); }
            }
            Backend b = lb.pick();
            int end = Math.max(t, busyUntil.get(b.name)) + cost.get(b.name);
            busyUntil.put(b.name, end);
            running.add(new Object[]{end, b});
            last = Math.max(last, end);
        }
        StringJoiner served = new StringJoiner(" ");
        for (Backend b : bs) served.add(b.name + "=" + b.served);
        return last + " | " + served;
    }

    interface Check { Object get() throws Exception; }

    static void check(String name, Object expected, Check actual) {
        try {
            Object got = actual.get();
            System.out.println(Objects.equals(expected, got) ? "PASS " + name : "FAIL " + name + ": expected <" + expected + "> but was <" + got + ">");
        } catch (Exception e) {
            System.out.println("FAIL " + name + ": threw " + e);
        }
    }
    // END CHECKS
}
