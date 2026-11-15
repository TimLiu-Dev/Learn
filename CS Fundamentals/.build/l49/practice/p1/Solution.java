import java.util.*;

public class Practice {
    record Session(String name, int start, int end) { }   // half-open [start, end)

    // The most sessions that don't overlap, as names in time order.
    static List<String> schedule(List<Session> sessions) {
        List<Session> byEnd = new ArrayList<>(sessions);
        byEnd.sort(Comparator.comparingInt(Session::end));     // earliest finish first
        List<String> taken = new ArrayList<>();
        int lastEnd = Integer.MIN_VALUE;
        for (Session s : byEnd) {
            if (s.start() >= lastEnd) {                         // >=: starting as the last one ends is fine
                taken.add(s.name());
                lastEnd = s.end();
            }
        }
        return taken;
    }

    // CHECKS
    static boolean overlaps(Session a, Session b) { return a.start() < b.end() && b.start() < a.end(); }

    static int bestSize(List<Session> s) {                    // brute force over all 2^n subsets
        int best = 0;
        for (int mask = 0; mask < (1 << s.size()); mask++) {
            List<Session> pick = new ArrayList<>();
            for (int i = 0; i < s.size(); i++) if ((mask & (1 << i)) != 0) pick.add(s.get(i));
            if (clashFree(pick)) best = Math.max(best, pick.size());
        }
        return best;
    }

    static boolean clashFree(List<Session> pick) {
        for (int i = 0; i < pick.size(); i++)
            for (int j = i + 1; j < pick.size(); j++) if (overlaps(pick.get(i), pick.get(j))) return false;
        return true;
    }

    public static void main(String[] args) {
        check("nothing to schedule gives nothing", List.of(), () -> schedule(List.of()));
        check("a long early session does not block two short ones", List.of("a", "b"),
            () -> schedule(List.of(new Session("long", 0, 10), new Session("a", 1, 2), new Session("b", 3, 4))));
        check("touching sessions both fit", List.of("x", "y"), () -> schedule(List.of(new Session("y", 3, 5), new Session("x", 0, 3))));
        check("the answer comes back in time order", List.of("early", "mid", "late"),
            () -> schedule(List.of(new Session("late", 8, 9), new Session("mid", 4, 6), new Session("early", 0, 2))));
        check("matches brute force on 300 random schedules", "all 300 optimal", () -> {
            Random rnd = new Random(49);
            for (int t = 0; t < 300; t++) {
                List<Session> s = new ArrayList<>();
                for (int i = 0; i < 8; i++) { int a = rnd.nextInt(20); s.add(new Session("c" + i, a, a + 1 + rnd.nextInt(8))); }
                List<Session> picked = new ArrayList<>();
                for (String name : schedule(s)) for (Session x : s) if (x.name().equals(name)) picked.add(x);
                if (!clashFree(picked) || picked.size() != bestSize(s)) return "schedule " + t + " gave " + picked;
            }
            return "all 300 optimal";
        });
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
