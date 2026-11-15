import java.time.*;
import java.util.*;

public class Practice {
    sealed interface Slot permits Exact, Skipped, Twice { }
    record Exact(ZonedDateTime at) implements Slot { }
    record Skipped(LocalDateTime requested, ZonedDateTime movedTo) implements Slot { }   // spring-forward gap
    record Twice(ZonedDateTime earlier, ZonedDateTime later) implements Slot { }         // fall-back overlap

    static Slot slot(LocalDateTime local, ZoneId zone) {
        throw new UnsupportedOperationException("TODO: ask the zone's rules how many offsets this local time has");
    }

    // e.g. 2026-07-01T09:30:00-04:00
    static String rfc3339(Instant t, ZoneId zone) {
        return t.atZone(zone).toOffsetDateTime().toString();
    }

    // CHECKS
    static final ZoneId NY = ZoneId.of("America/New_York");

    static ZonedDateTime ny(String text) { return ZonedDateTime.parse(text + "[America/New_York]"); }

    public static void main(String[] args) {
        check("an ordinary time is exact", new Exact(ny("2026-07-01T09:30-04:00")), () -> slot(LocalDateTime.of(2026, 7, 1, 9, 30), NY));
        LocalDateTime gap = LocalDateTime.of(2026, 3, 8, 2, 30);
        check("02:30 on the spring-forward night is skipped", new Skipped(gap, ny("2026-03-08T03:30-04:00")), () -> slot(gap, NY));
        check("01:30 on the fall-back night happens twice, an hour apart",
                new Twice(ny("2026-11-01T01:30-04:00"), ny("2026-11-01T01:30-05:00")), () -> slot(LocalDateTime.of(2026, 11, 1, 1, 30), NY));
        ZoneId paris = ZoneId.of("Europe/Paris");
        LocalDateTime parisGap = LocalDateTime.of(2026, 3, 29, 2, 15);
        check("Paris has its own gap, on another date",
                new Skipped(parisGap, ZonedDateTime.of(LocalDateTime.of(2026, 3, 29, 3, 15), paris)), () -> slot(parisGap, paris));
        check("New York's gap date is an ordinary night in Paris", new Exact(ZonedDateTime.of(gap, paris)), () -> slot(gap, paris));
        check("rfc3339 keeps the seconds and the offset", "2026-07-01T09:30:00-04:00", () -> rfc3339(Instant.parse("2026-07-01T13:30:00Z"), NY));
        check("rfc3339 writes Z for UTC", "2026-11-01T06:30:00Z", () -> rfc3339(Instant.parse("2026-11-01T06:30:00Z"), ZoneOffset.UTC));
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
