import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// Lamport clocks and vector clocks, run over the same script of events.
public class Clocks {
    static class Lamport {
        private long time = 0;
        long tick() { time++; return time; }                       // IR1: count every event
        long receive(long stamp) {                                 // IR2(b): jump past the stamp
            time = Math.max(time, stamp) + 1;
            return time;
        }
    }

    static class Vector {
        private final int[] v;
        Vector(int n) { v = new int[n]; }
        int[] tick(int me) { v[me]++; return v.clone(); }
        int[] receive(int me, int[] other) {
            for (int i = 0; i < v.length; i++) v[i] = Math.max(v[i], other[i]);   // learn what the sender knew
            return tick(me);
        }
    }

    enum Order { BEFORE, AFTER, EQUAL, CONCURRENT }

    static Order compare(int[] a, int[] b) {
        boolean le = true, ge = true;
        for (int i = 0; i < a.length; i++) {
            if (a[i] > b[i]) le = false;
            if (a[i] < b[i]) ge = false;
        }
        if (le && ge) return Order.EQUAL;
        if (le) return Order.BEFORE;
        if (ge) return Order.AFTER;
        return Order.CONCURRENT;
    }

    // One step of the script: process p does a local event, sends msg, or receives msg.
    sealed interface Step permits Local, Send, Recv { int p(); String name(); }
    record Local(int p, String name) implements Step {}
    record Send(int p, String msg) implements Step { public String name() { return "send " + msg; } }
    record Recv(int p, String msg) implements Step { public String name() { return "recv " + msg; } }

    record Stamps(long lamport, int[] vector) {}
    record Event(String id, int p, long lamport, int[] vector) {}

    static String show(int[] v) {
        return Arrays.stream(v).mapToObj(String::valueOf).collect(Collectors.joining(",", "[", "]"));
    }

    public static void main(String[] args) {
        List<Step> script = List.of(
            new Local(0, "a"), new Send(0, "m1"),
            new Local(1, "b"), new Recv(1, "m1"), new Send(1, "m2"),
            new Local(2, "c"), new Recv(2, "m2"),
            new Local(0, "d"));
        Lamport[] lamports = {new Lamport(), new Lamport(), new Lamport()};
        Vector[] vectors = {new Vector(3), new Vector(3), new Vector(3)};
        Map<String, Stamps> inFlight = new HashMap<>();              // message -> the stamps it carries
        List<Event> events = new ArrayList<>();

        for (Step s : script) {
            Stamps st = switch (s) {
                case Local l -> new Stamps(lamports[l.p()].tick(), vectors[l.p()].tick(l.p()));
                case Send m -> {
                    Stamps sent = new Stamps(lamports[m.p()].tick(), vectors[m.p()].tick(m.p()));
                    inFlight.put(m.msg(), sent);
                    yield sent;
                }
                case Recv r -> {
                    Stamps got = inFlight.get(r.msg());
                    yield new Stamps(lamports[r.p()].receive(got.lamport()), vectors[r.p()].receive(r.p(), got.vector()));
                }
            };
            events.add(new Event("P" + (s.p() + 1) + " " + s.name(), s.p(), st.lamport(), st.vector()));
        }

        System.out.println("== One run, two kinds of logical clock");
        System.out.println(String.format("%-12s %7s  %s", "event", "Lamport", "vector"));
        for (Event e : events) System.out.println(String.format("%-12s %7d  %s", e.id(), e.lamport(), show(e.vector())));

        System.out.println();
        System.out.println("== What each clock can tell you about a pair of events");
        Map<String, Event> byId = new HashMap<>();
        for (Event e : events) byId.put(e.id(), e);
        String[][] pairs = {{"P1 send m1", "P2 recv m1"}, {"P1 a", "P3 recv m2"},
                            {"P2 b", "P1 d"}, {"P1 d", "P3 recv m2"}, {"P1 a", "P3 c"}};
        for (String[] pair : pairs) {
            Event a = byId.get(pair[0]), b = byId.get(pair[1]);
            String sign = a.lamport() < b.lamport() ? "<" : a.lamport() > b.lamport() ? ">" : "=";
            System.out.println(String.format("%-10s vs %-10s  Lamport %d %s %d   vector %s",
                pair[0], pair[1], a.lamport(), sign, b.lamport(), compare(a.vector(), b.vector())));
        }

        System.out.println();
        System.out.println("== A total order: sort by (Lamport time, process number)");
        System.out.println(events.stream()
            .sorted(Comparator.comparingLong(Event::lamport).thenComparingInt(Event::p))
            .map(e -> e.id() + "@" + e.lamport()).collect(Collectors.joining("  ")));

        System.out.println();
        System.out.println("== Version vectors on two cart replicas (entry 0 counts A's writes, entry 1 B's)");
        int[] base = {1, 0};
        int[] onA = {2, 0};                               // A adds lamp during the partition
        int[] onB = {1, 1};                               // B adds pen during the partition
        System.out.println("[book,lamp] " + show(onA) + " vs [book,pen] " + show(onB) + ": " + compare(onA, onB) + " -> keep both, merge");
        int[] merged = {Math.max(onA[0], onB[0]), Math.max(onA[1], onB[1]) + 1};   // B writes the merge
        System.out.println("[book,lamp,pen] " + show(merged) + " vs [book,lamp] " + show(onA) + ": " + compare(merged, onA) + " -> safe to overwrite");
        System.out.println("[book] " + show(base) + " vs [book,lamp,pen] " + show(merged) + ": " + compare(base, merged) + " -> a stale write, ignore it");
    }
}
