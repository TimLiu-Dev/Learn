import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Practice {
    // IMF-fixdate: "Sun, 06 Nov 1994 08:49:37 GMT". English names whatever the JVM's locale, and "dd", not "d".
    static final DateTimeFormatter IMF_FIXDATE =
        DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss 'GMT'", Locale.ENGLISH).withZone(ZoneOffset.UTC);

    static Map<String, String> retirementHeaders(Instant deprecatedAt, Instant sunsetAt, String docs) {
        if (sunsetAt.isBefore(deprecatedAt))
            throw new IllegalArgumentException("sunset " + sunsetAt + " is before deprecation " + deprecatedAt);
        return Map.of(
            "Deprecation", "@" + deprecatedAt.getEpochSecond(),
            "Sunset", IMF_FIXDATE.format(sunsetAt),
            "Link", "<" + docs + ">; rel=\"deprecation\"");
    }

    // CHECKS
    static final String DOCS = "https://shelf.example.com/docs/changes";

    public static void main(String[] args) {
        check("Shelf's own dates give Exercise 5's headers",
            Map.of("Deprecation", "@1798761600", "Sunset", "Sat, 01 Jan 2028 00:00:00 GMT", "Link", "<" + DOCS + ">; rel=\"deprecation\""),
            () -> retirementHeaders(Instant.parse("2027-01-01T00:00:00Z"), Instant.parse("2028-01-01T00:00:00Z"), DOCS));
        check("an early-month day still has two digits", "Sun, 05 Mar 2028 09:07:03 GMT",
            () -> retirementHeaders(Instant.parse("2027-11-09T14:30:00Z"), Instant.parse("2028-03-05T09:07:03Z"), DOCS).get("Sunset"));
        check("the same instant is allowed", "@1798761600",
            () -> retirementHeaders(Instant.parse("2027-01-01T00:00:00Z"), Instant.parse("2027-01-01T00:00:00Z"), DOCS).get("Deprecation"));
        check("a sunset before the deprecation is refused", "IllegalArgumentException", () -> {
            try {
                retirementHeaders(Instant.parse("2028-01-01T00:00:00Z"), Instant.parse("2027-12-31T23:59:59Z"), DOCS);
                return "no exception";
            } catch (IllegalArgumentException e) {
                return "IllegalArgumentException";
            }
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
