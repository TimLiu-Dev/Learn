import java.util.*;

public class Practice {
    // ms since JVM start, compile ID, OSR (%), tier (null when tiering is off), method, bytecode size, "made not entrant"
    record Compile(int ms, int id, boolean osr, Integer tier, String method, int bytes, boolean retired) {}

    // e.g. "10   10 %     4       Hot::work @ 4 (29 bytes)"
    static Compile parse(String line) {
        String[] tokens = line.trim().split("\\s+");
        int methodAt = 0;
        while (!tokens[methodAt].contains("::")) methodAt++;
        throw new UnsupportedOperationException("TODO: read the fields; the tokens before methodAt are time, id, flags and maybe a tier");
    }

    static Integer c2Landed(List<String> lines, String method) {
        throw new UnsupportedOperationException("TODO: time of the first tier-4, non-OSR, non-retired compile of method");
    }

    // CHECKS
    static final List<String> HOT_LOG = List.of(
        "10    8 %     3       Hot::work @ 4 (29 bytes)",
        "10    9       3       Hot::work (29 bytes)",
        "10   10 %     4       Hot::work @ 4 (29 bytes)",
        "10    8 %     3       Hot::work @ 4 (29 bytes)   made not entrant: OSR invalidation of lower level",
        "19  105       4       Hot::work (29 bytes)",
        "20    9       3       Hot::work (29 bytes)   made not entrant: not used");

    public static void main(String[] args) {
        check("an OSR compile at tier 4", new Compile(10, 10, true, 4, "Hot::work", 29, false), () -> parse(HOT_LOG.get(2)));
        check("a full-method compile is not OSR", new Compile(19, 105, false, 4, "Hot::work", 29, false), () -> parse(HOT_LOG.get(4)));
        check("made not entrant marks retired code", new Compile(20, 9, false, 3, "Hot::work", 29, true), () -> parse(HOT_LOG.get(5)));
        check("no tier column when tiered compilation is off", new Compile(10, 4, true, null, "Hot::work", 29, false),
              () -> parse("10    4 %           Hot::work @ 4 (29 bytes)"));
        check("C2's full-method compile landed at 19 ms, not the OSR one at 10", 19, () -> c2Landed(HOT_LOG, "Hot::work"));
        List<String> hunt = List.of(
            "17   79       3       HotHunt::collatzSteps (42 bytes)",
            "17   80 %     4       HotHunt::collatzSteps @ 2 (42 bytes)",
            "23  141       4       HotHunt::collatzSteps (42 bytes)",
            "23   79       3       HotHunt::collatzSteps (42 bytes)   made not entrant: not used");
        check("the final exercise's log", 23, () -> c2Landed(hunt, "HotHunt::collatzSteps"));
        check("a method that never reached C2", null, () -> c2Landed(hunt, "Hot::work"));
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
