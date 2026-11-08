import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Practice {
    // extends Exception, so in Java it is checked
    static class SeatTakenException extends Exception {
        final int seat;
        final String holder;

        SeatTakenException(int seat, String holder) {
            super("seat " + seat + " is taken by " + holder);
            this.seat = seat;
            this.holder = holder;
        }
    }

    static class Cinema {
        private final int seats;
        boolean open = false;
        private final Map<Integer, String> booked = new HashMap<>();

        Cinema(int seats) { this.seats = seats; }

        void book(int seat, String name) throws SeatTakenException {
            throw new UnsupportedOperationException("TODO: validate the seat, check open, refuse a taken seat, then book");
        }

        String holderOf(int seat) { return booked.get(seat); }
    }

    // The number in text, or seat 1 if text isn't a number.
    static int seatOrDefault(String text) {
        throw new UnsupportedOperationException("TODO: parse, or fall back to 1");
    }

    // CHECKS
    static Cinema openCinema() { Cinema c = new Cinema(10); c.open = true; return c; }

    static String outcome(Check c) {
        try {
            c.get();
            return "no exception";
        } catch (Exception e) {
            return e.getClass().getSimpleName();
        }
    }

    public static void main(String[] args) {
        check("a free seat can be booked", "Ada", () -> { Cinema c = openCinema(); c.book(5, "Ada"); return c.holderOf(5); });
        check("a seat outside the hall is the caller's bug", "IllegalArgumentException", () -> outcome(() -> { openCinema().book(11, "Ada"); return null; }));
        check("booking while closed is a state problem", "IllegalStateException", () -> outcome(() -> { new Cinema(10).book(5, "Ada"); return null; }));
        check("a bad seat is reported before the closed state", "IllegalArgumentException", () -> outcome(() -> { new Cinema(10).book(0, "Ada"); return null; }));
        check("a taken seat names its holder", "5 Ada", () -> {
            Cinema c = openCinema();
            c.book(5, "Ada");
            try { c.book(5, "Bob"); return "no exception"; } catch (SeatTakenException e) { return e.seat + " " + e.holder; }
        });
        check("seatOrDefault reads a number", 7, () -> seatOrDefault(" 7 "));
        check("seatOrDefault falls back to seat 1", 1, () -> seatOrDefault("seven"));
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
