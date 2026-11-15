import java.util.*;

public class Practice {
    // One step of a script: process p (0, 1 or 2) does a local event, sends msg, or receives msg.
    sealed interface Step permits Local, Send, Recv { int p(); }
    record Local(int p) implements Step { }
    record Send(int p, String msg) implements Step { }
    record Recv(int p, String msg) implements Step { }

    enum Order { BEFORE, AFTER, EQUAL, CONCURRENT }

    // The Lamport time of every step, in script order.
    static List<Long> lamportStamps(List<Step> script) {
        long[] clock = new long[3];
        Map<String, Long> stampOf = new HashMap<>();     // message -> the stamp it carries
        List<Long> stamps = new ArrayList<>();
        for (Step s : script) {
            long now = switch (s) {
                case Local l -> clock[l.p()] + 1;
                case Send m -> {
                    stampOf.put(m.msg(), clock[m.p()] + 1);
                    yield clock[m.p()] + 1;
                }
                case Recv r -> Math.max(clock[r.p()], stampOf.get(r.msg())) + 1;   // never backwards
            };
            clock[s.p()] = now;
            stamps.add(now);
        }
        return stamps;
    }

    // How the event stamped a relates to the event stamped b (arrays of the same length).
    static Order compare(int[] a, int[] b) {
        boolean le = true, ge = true;
        for (int i = 0; i < a.length; i++) {
            if (a[i] > b[i]) le = false;
            if (a[i] < b[i]) ge = false;
        }
        if (le && ge) return Order.EQUAL;
        if (le) return Order.BEFORE;
        if (ge) return Order.AFTER;
        return Order.CONCURRENT;                          // each is ahead somewhere
    }

    // CHECKS
    public static void main(String[] args) {
        check("local events and sends count up on each process", List.of(1L, 2L, 1L),
            () -> lamportStamps(List.of(new Local(0), new Send(0, "m"), new Local(1))));
        check("a receive jumps past the message's stamp", List.of(1L, 2L, 3L, 4L),
            () -> lamportStamps(List.of(new Local(0), new Local(0), new Send(0, "m"), new Recv(1, "m"))));
        check("a receive never moves a clock backwards", List.of(1L, 1L, 2L, 3L, 4L, 5L),
            () -> lamportStamps(List.of(new Send(0, "m"), new Local(1), new Local(1), new Local(1), new Local(1), new Recv(1, "m"))));
        check("the lesson's script gives the lesson's stamps", List.of(1L, 2L, 1L, 3L, 4L, 1L, 5L, 3L),
            () -> lamportStamps(List.of(new Local(0), new Send(0, "m1"), new Local(1), new Recv(1, "m1"), new Send(1, "m2"),
                                        new Local(2), new Recv(2, "m2"), new Local(0))));
        check("compare spots before, after and equal", List.of(Order.BEFORE, Order.AFTER, Order.EQUAL),
            () -> List.of(compare(new int[]{1, 0, 0}, new int[]{2, 1, 0}), compare(new int[]{2, 3, 2}, new int[]{2, 3, 0}),
                          compare(new int[]{2, 2, 0}, new int[]{2, 2, 0})));
        check("compare spots concurrent events", List.of(Order.CONCURRENT, Order.CONCURRENT),
            () -> List.of(compare(new int[]{2, 1, 0}, new int[]{1, 1, 3}), compare(new int[]{0, 1, 0}, new int[]{3, 0, 0})));
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
