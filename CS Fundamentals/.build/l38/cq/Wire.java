import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;

public class Wire {
    static class Stamp {
        private Clock clock;

        Stamp(Clock clock) {
            clock = clock;
        }

        String today() { return LocalDate.now(clock).toString(); }
    }

    public static void main(String[] args) {
        Clock fixed = Clock.fixed(Instant.parse("2026-10-24T09:00:00Z"), ZoneOffset.UTC);
        try {
            System.out.println(new Stamp(fixed).today());
        } catch (NullPointerException e) {
            System.out.println("NullPointerException");
        }
    }
}
