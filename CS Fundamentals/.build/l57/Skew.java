public class Skew {
    // Each node reads its OWN clock: true time plus an error (offset) in ms.
    // "True time" exists only inside this simulation; real nodes never see it.
    static class Node {
        final String name;
        long offsetMs;
        Node(String name, long offsetMs) { this.name = name; this.offsetMs = offsetMs; }
        long now(long trueMs) { return trueMs + offsetMs; }
    }

    record Versioned(String value, long ts, String node) {}

    // A last-write-wins register: keep whichever version has the bigger timestamp.
    static class Replica {
        final Node node;
        Versioned current;
        Replica(Node node) { this.node = node; }
        Versioned write(String value, long trueMs) {
            Versioned v = new Versioned(value, node.now(trueMs), node.name);
            receive(v);
            return v;
        }
        void receive(Versioned v) {
            if (current == null || v.ts() > current.ts()) current = v;
        }
    }

    // Two edits 'gapMs' apart, the second on the replica whose clock is 'skewMs' slow.
    static boolean lostUpdate(long skewMs, long gapMs) {
        Replica a = new Replica(new Node("A", 0));
        Replica b = new Replica(new Node("B", -skewMs));
        Versioned first = a.write("Ana", 1_000);
        Versioned second = b.write("Ana Lima", 1_000 + gapMs);
        a.receive(second); b.receive(first);              // replicate both ways
        return !a.current.value().equals("Ana Lima");
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println("== Last write wins with a clock that is 300 ms slow");
        Replica a = new Replica(new Node("A", 0));
        Replica b = new Replica(new Node("B", -300));
        Versioned first = a.write("Ana", 1_000);
        System.out.println("true 1000 ms  user saves name=Ana      on A, A's clock says " + first.ts());
        Versioned second = b.write("Ana Lima", 1_100);
        System.out.println("true 1100 ms  user saves name=Ana Lima on B, B's clock says " + second.ts());
        a.receive(second); b.receive(first);
        System.out.println("after replication: A=" + a.current.value() + "  B=" + b.current.value());
        System.out.println("the later edit lost: " + second.ts() + " < " + first.ts() + ", so LWW kept the older value");

        System.out.println();
        System.out.println("== Lost? (rows: clock skew, columns: gap between the two edits)");
        long[] gaps = {10, 50, 100, 500};
        StringBuilder head = new StringBuilder("skew   ");
        for (long g : gaps) head.append(String.format("%7s", g + "ms"));
        System.out.println(head);
        for (long skew : new long[]{0, 20, 100, 300}) {
            StringBuilder row = new StringBuilder(String.format("%4dms ", skew));
            for (long g : gaps) row.append(String.format("%7s", lostUpdate(skew, g) ? "LOST" : "ok"));
            System.out.println(row);
        }

        System.out.println();
        System.out.println("== A 5 s timeout measured with the time-of-day clock, while NTP steps it");
        for (long stepMs : new long[]{-2_000, +2_000}) {
            Node node = new Node("C", 0);
            long start = node.now(0);
            long t = 0;
            while (true) {
                t += 100;
                if (t == 1_000) node.offsetMs += stepMs;        // NTP corrects the clock at true 1 s
                if (node.now(t) - start >= 5_000) break;
            }
            System.out.println(String.format("step %+d ms at 1 s: timeout fired after %.1f s of real time", stepMs, t / 1000.0));
        }

        System.out.println();
        System.out.println("== The fix for durations: a monotonic clock (real run)");
        long t0 = System.nanoTime();
        Thread.sleep(50);
        System.out.println("System.nanoTime():    slept 50 ms, measured " + (System.nanoTime() - t0) / 1_000_000 + " ms");
    }
}
