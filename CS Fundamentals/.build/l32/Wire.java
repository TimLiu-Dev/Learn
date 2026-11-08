import java.time.*;
import tools.jackson.databind.cfg.DateTimeFeature;
import tools.jackson.databind.json.JsonMapper;

public class Wire {
    // A future event: store what the humans agreed on (local time + zone), not an instant
    public record Meeting(String title, LocalDateTime start, ZoneId zone) {
        Instant startInstant() { return start.atZone(zone).toInstant(); }  // computed with today's rules
    }
    public record Stamp(ZonedDateTime at) {}

    public static void main(String[] args) {
        var mapper = JsonMapper.builder().build();
        var m = new Meeting("Standup", LocalDateTime.of(2026, 11, 2, 9, 0), ZoneId.of("America/New_York"));
        String json = mapper.writeValueAsString(m);
        System.out.println(json);
        System.out.println("starts at " + mapper.readValue(json, Meeting.class).startInstant());

        // RFC 3339 strings carry an offset; parse them as OffsetDateTime or Instant
        String wire = "2026-11-01T01:30:00-05:00";
        System.out.println("offset  " + OffsetDateTime.parse(wire));
        System.out.println("instant " + Instant.parse(wire));

        // What does Jackson do with a ZonedDateTime round trip?
        var stamp = new Stamp(ZonedDateTime.of(2026, 11, 1, 1, 30, 0, 0, ZoneId.of("America/New_York")));
        String s = mapper.writeValueAsString(stamp);
        System.out.println(s);
        Stamp back = mapper.readValue(s, Stamp.class);
        System.out.println("back    " + back.at() + "   equal? " + back.equals(stamp)
            + "   same instant? " + back.at().toInstant().equals(stamp.at().toInstant()));

        // Keep the zone id: write it, and don't rewrite parsed values into UTC
        var keepZone = JsonMapper.builder()
            .enable(DateTimeFeature.WRITE_DATES_WITH_ZONE_ID)
            .disable(DateTimeFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)
            .build();
        String z = keepZone.writeValueAsString(stamp);
        System.out.println(z);
        System.out.println("back    " + keepZone.readValue(z, Stamp.class).at()
            + "   equal? " + keepZone.readValue(z, Stamp.class).equals(stamp));
    }
}
