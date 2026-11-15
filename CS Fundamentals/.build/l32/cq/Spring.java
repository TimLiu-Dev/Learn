import java.time.*;

public class Spring {
    public static void main(String[] args) {
        ZoneId ny = ZoneId.of("America/New_York");
        ZonedDateTime sat = ZonedDateTime.of(2026, 3, 7, 12, 0, 0, 0, ny);   // the day before clocks spring forward

        ZonedDateTime nextDay = sat.plusDays(1);
        System.out.println(Duration.between(sat, nextDay).toHours() + " " + sat.plusHours(24).getHour());
    }
}
