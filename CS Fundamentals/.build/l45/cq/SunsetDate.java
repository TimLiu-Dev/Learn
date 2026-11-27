import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class SunsetDate {
    public static void main(String[] args) {
        var sunset = Instant.parse("2027-03-01T00:00:00Z").atOffset(ZoneOffset.UTC);
        System.out.println(DateTimeFormatter.RFC_1123_DATE_TIME.format(sunset));
    }
}
