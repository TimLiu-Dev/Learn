import java.time.*;

public class Dst {
    public static void main(String[] args) {
        ZoneId ny = ZoneId.of("America/New_York");

        // Gap: on 2026-03-08 New York clocks jump from 02:00 to 03:00
        LocalDateTime gap = LocalDateTime.of(2026, 3, 8, 2, 30);
        System.out.println("gap     " + gap + " -> " + ZonedDateTime.of(gap, ny));
        System.out.println("offsets " + ny.getRules().getValidOffsets(gap));  // none: 02:30 never happens

        // Overlap: on 2026-11-01 clocks fall back from 02:00 to 01:00, so 01:30 happens twice
        LocalDateTime overlap = LocalDateTime.of(2026, 11, 1, 1, 30);
        System.out.println("offsets " + ny.getRules().getValidOffsets(overlap));  // two: 01:30 happens twice
        ZonedDateTime first = ZonedDateTime.of(overlap, ny);
        ZonedDateTime second = first.withLaterOffsetAtOverlap();
        System.out.println("first   " + first);
        System.out.println("second  " + second);
        System.out.println("between " + Duration.between(first, second));

        // "Tomorrow, same time" is not "24 hours later" across a DST change
        ZonedDateTime sat = ZonedDateTime.of(LocalDateTime.of(2026, 10, 31, 9, 0), ny);
        System.out.println("+1 day  " + sat.plusDays(1));
        System.out.println("+24 h   " + sat.plusHours(24));
    }
}
