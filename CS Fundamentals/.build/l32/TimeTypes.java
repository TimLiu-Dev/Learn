import java.time.*;
import java.time.zone.ZoneRulesProvider;

public class TimeTypes {
    public static void main(String[] args) {
        ZoneId ny = ZoneId.of("America/New_York");
        Instant instant = Instant.parse("2026-07-01T13:30:00Z");   // one moment on the timeline
        ZonedDateTime zoned = instant.atZone(ny);                  // that moment, as New York sees it
        System.out.println("Instant        " + instant);
        System.out.println("ZonedDateTime  " + zoned);
        System.out.println("OffsetDateTime " + zoned.toOffsetDateTime());
        System.out.println("LocalDateTime  " + zoned.toLocalDateTime() + "   <- no zone: not a moment");
        System.out.println("LocalDate      " + zoned.toLocalDate());
        System.out.println("Tokyo, same    " + instant.atZone(ZoneId.of("Asia/Tokyo")));
        System.out.println("tzdb version   " + ZoneRulesProvider.getVersions("America/New_York").lastKey());
    }
}
