import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Supplier;

public class Practice {
    record Hold(String id, long bookId, String member, long placedAtMillis) {}

    static final long HOLD_MILLIS = 3L * 24 * 60 * 60 * 1000;    // a hold lasts 3 days

    static final class HoldShelf {
        private final Clock clock;                 // TODO: read the time from here, not System.currentTimeMillis()
        private final Supplier<String> newId;      // TODO: take ids from here, not UUID.randomUUID()
        private final List<Hold> holds = new ArrayList<>();

        HoldShelf(Clock clock, Supplier<String> newId) {
            this.clock = clock;
            this.newId = newId;
        }

        Hold place(long bookId, String member) {
            Hold hold = new Hold(UUID.randomUUID().toString(), bookId, member, System.currentTimeMillis());
            holds.add(hold);
            return hold;
        }

        // Holds younger than 3 days. Older ones have expired: the book goes back on the open shelf.
        List<Hold> waiting() {
            return holds.stream().filter(h -> System.currentTimeMillis() - h.placedAtMillis() < HOLD_MILLIS).toList();
        }

        // True if the hold is still waiting; it then leaves the shelf. False if it's unknown or expired.
        boolean pickUp(String id) {
            throw new UnsupportedOperationException("TODO: find the hold among the waiting ones, remove it");
        }
    }

    // CHECKS
    // A hand-written fake clock: time moves only when a check says so.
    static final class FakeClock extends Clock {
        private Instant now;
        FakeClock(Instant now) { this.now = now; }
        @Override public Instant instant() { return now; }
        @Override public ZoneId getZone() { return ZoneOffset.UTC; }
        @Override public Clock withZone(ZoneId zone) { return this; }
        void advance(Duration by) { now = now.plus(by); }
    }

    static final Instant START = Instant.parse("2026-10-21T10:00:00Z");
    static FakeClock clock;

    static HoldShelf freshShelf() {                       // ids H1, H2, …
        clock = new FakeClock(START);
        int[] n = {0};
        return new HoldShelf(clock, () -> "H" + (++n[0]));
    }

    public static void main(String[] args) {
        check("a hold records the injected time and id", "H1 at " + START.toEpochMilli(), () -> {
            Hold hold = freshShelf().place(7, "ada");
            return hold.id() + " at " + hold.placedAtMillis();
        });
        check("a hold still waits one millisecond before 3 days", List.of("ada"), () -> {
            HoldShelf shelf = freshShelf();
            shelf.place(7, "ada");
            clock.advance(Duration.ofDays(3).minusMillis(1));
            return shelf.waiting().stream().map(Hold::member).toList();
        });
        check("a hold expires at exactly 3 days", List.of("alan"), () -> {
            HoldShelf shelf = freshShelf();
            shelf.place(7, "ada");
            clock.advance(Duration.ofDays(1));
            shelf.place(8, "alan");
            clock.advance(Duration.ofDays(2));
            return shelf.waiting().stream().map(Hold::member).toList();
        });
        check("picking up in time removes the hold, once", "true [] false", () -> {
            HoldShelf shelf = freshShelf();
            Hold hold = shelf.place(7, "ada");
            clock.advance(Duration.ofDays(1));
            boolean first = shelf.pickUp(hold.id());
            return first + " " + shelf.waiting() + " " + shelf.pickUp(hold.id());
        });
        check("an expired or unknown hold cannot be picked up", "false false", () -> {
            HoldShelf shelf = freshShelf();
            Hold hold = shelf.place(7, "ada");
            clock.advance(Duration.ofDays(4));
            return shelf.pickUp(hold.id()) + " " + shelf.pickUp("H99");
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
