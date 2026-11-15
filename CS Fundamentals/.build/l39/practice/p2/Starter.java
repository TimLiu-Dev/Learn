import java.util.*;

public class Practice {
    record Booking(String room, int start, int end, int people) {
        Booking {   // a compact constructor: Java's version of Kotlin's init block
            throw new UnsupportedOperationException("TODO: reject a blank room, hours outside 8..18, start >= end, and nobody attending");
        }

        static Booking allDay(String room) {
            throw new UnsupportedOperationException("TODO: 8 to 18");
        }

        static Booking parse(String text) {
            throw new UnsupportedOperationException("TODO: \"Ada 9-11 x4\" -> new Booking(\"Ada\", 9, 11, 4); without \"x4\", one person");
        }

        // A classic Builder for Java callers. It holds no rules: build() goes through the constructor.
        static Builder builder(String room) { return new Builder(room); }

        static final class Builder {
            private final String room;
            private int start = 8, people = 1;
            private Integer end = null;                          // null means "start + 1"
            private Builder(String room) { this.room = room; }
            Builder from(int hour) { start = hour; return this; }
            Builder to(int hour) { end = hour; return this; }
            Builder people(int n) { people = n; return this; }
            Booking build() { return new Booking(room, start, end == null ? start + 1 : end, people); }
        }
    }

    // CHECKS
    static String outcome(Check make) {
        try { return String.valueOf(make.get()); }
        catch (IllegalArgumentException e) { return "rejected"; }
        catch (Exception e) { return "threw " + e; }
    }

    public static void main(String[] args) {
        check("the builder fills in the optional fields", "Booking[room=Ada, start=9, end=10, people=1]", () -> Booking.builder("Ada").from(9).build().toString());
        check("the builder sets every field", "Booking[room=Ada, start=9, end=12, people=3]", () -> Booking.builder("Ada").from(9).to(12).people(3).build().toString());
        check("the constructor rejects every broken rule", List.of("rejected", "rejected", "rejected", "rejected", "rejected", "rejected"), () -> List.of(
            outcome(() -> new Booking(" ", 9, 10, 1)),
            outcome(() -> new Booking("Ada", 7, 8, 1)),
            outcome(() -> new Booking("Ada", 17, 19, 1)),
            outcome(() -> new Booking("Ada", 10, 10, 1)),
            outcome(() -> new Booking("Ada", 12, 10, 1)),
            outcome(() -> new Booking("Ada", 9, 10, 0))));
        check("the builder is checked too", "rejected", () -> outcome(() -> Booking.builder("Ada").from(10).to(9).build()));
        check("allDay books the whole opening day", "Booking[room=Turing, start=8, end=18, people=1]", () -> Booking.allDay("Turing").toString());
        check("parse reads hours and an optional head count", "[Booking[room=Ada, start=9, end=11, people=4], Booking[room=Turing, start=14, end=15, people=1]]",
            () -> List.of(Booking.parse("Ada 9-11 x4"), Booking.parse("Turing 14-15")).toString());
        check("parse goes through the same rules", List.of("rejected", "rejected", "rejected"), () -> List.of(
            outcome(() -> Booking.parse("Ada 11-9")), outcome(() -> Booking.parse("Ada 9-11 x0")), outcome(() -> Booking.parse("Ada nine-11"))));
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
