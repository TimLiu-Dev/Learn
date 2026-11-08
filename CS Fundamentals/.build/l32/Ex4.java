import java.time.*;

public class Ex4 {
    // Due = same local time, 14 calendar days later, in the library's zone
    static ZonedDateTime dueAt(Instant borrowed, ZoneId zone, long days) {
        return borrowed.atZone(zone).plusDays(days);
    }

    public static void main(String[] args) {
        ZoneId ny = ZoneId.of("America/New_York");
        Instant borrowed = Instant.parse("2026-10-25T14:00:00Z");     // 10:00 in New York
        ZonedDateTime due = dueAt(borrowed, ny, 14);
        System.out.println("borrowed " + borrowed.atZone(ny));
        System.out.println("due      " + due);
        System.out.println("due UTC  " + due.toInstant());
        System.out.println("hours    " + Duration.between(borrowed, due.toInstant()).toHours());  // 14 x 24 = 336?
        System.out.println("naive    " + borrowed.plus(Duration.ofDays(14)).atZone(ny));         // 24-hour days
    }
}
