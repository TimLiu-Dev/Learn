import java.util.*;
import java.util.stream.*;

public class Practice {
    enum Verdict { WAIT, PROMOTE, ROLLBACK }

    record Stats(int requests, int errors) {
        double errorRate() { return requests == 0 ? 0.0 : (double) errors / requests; }
    }

    /** True if this user is in the canary slice. Same user, same percent: same answer, every time. */
    static boolean inCanary(String userId, int percent) {
        return Math.floorMod(userId.hashCode(), 100) < percent;   // floorMod is never negative, even for MIN_VALUE
    }

    /** Compare the canary with the control group (the servers still on the old release). */
    static Verdict verdict(Stats control, Stats canary, int minRequests, double tolerance) {
        if (canary.requests() < minRequests) return Verdict.WAIT;    // too little evidence either way
        if (canary.errorRate() > control.errorRate() + tolerance) return Verdict.ROLLBACK;
        return Verdict.PROMOTE;
    }

    // CHECKS
    static final List<String> USERS = IntStream.rangeClosed(1, 10_000).mapToObj(i -> "user" + i).toList();

    public static void main(String[] args) {
        check("at 0 percent nobody gets the canary", 0L, () -> USERS.stream().filter(u -> inCanary(u, 0)).count());
        check("at 100 percent everybody does", 10_000L, () -> USERS.stream().filter(u -> inCanary(u, 100)).count());
        check("about a tenth of users at 10 percent, the same ones every time", true, () -> {
            List<String> first = USERS.stream().filter(u -> inCanary(u, 10)).toList();
            return first.size() >= 800 && first.size() <= 1200 && first.equals(USERS.stream().filter(u -> inCanary(u, 10)).toList());
        });
        check("raising the percent never moves a canary user back", true,
            () -> USERS.stream().filter(u -> inCanary(u, 10)).allMatch(u -> inCanary(u, 50)));
        check("a user whose hashCode is Integer.MIN_VALUE stays out at 0 percent", false, () -> inCanary("polygenelubricants", 0));
        Stats control = new Stats(10_000, 40);
        check("the verdict waits with too few requests", Verdict.WAIT, () -> verdict(control, new Stats(60, 12), 100, 0.01));
        check("the verdict rolls back a bad canary", Verdict.ROLLBACK, () -> verdict(control, new Stats(200, 40), 100, 0.01));
        check("the verdict promotes a good canary", Verdict.PROMOTE, () -> verdict(control, new Stats(200, 1), 100, 0.01));
        check("exactly at the tolerance is a promote", Verdict.PROMOTE, () -> verdict(new Stats(1000, 0), new Stats(100, 1), 100, 0.01));
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
