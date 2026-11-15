import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Practice {
    record Reservation(long id, long bookId, String member) {}

    // The policy owns this interface (DIP). Every implementation must keep these promises.
    interface ReservationStore {
        /** Adds to the back of this book's queue. Ids are 1, 2, 3… across all books, never reused. */
        Reservation add(long bookId, String member);

        /** This book's reservations, oldest first. A snapshot: later changes don't show up in it. */
        List<Reservation> queue(long bookId);

        /** Removes the reservation. False if there's no such id, or it was already cancelled. */
        boolean cancel(long id);
    }

    // The policy. It's finished: don't change it.
    static final class HoldDesk {
        private final ReservationStore store;
        HoldDesk(ReservationStore store) { this.store = store; }

        Reservation reserve(long bookId, String member) {
            if (store.queue(bookId).stream().anyMatch(r -> r.member().equals(member))) return null;
            return store.add(bookId, member);
        }

        String nextInLine(long bookId) {
            List<Reservation> q = store.queue(bookId);
            return q.isEmpty() ? null : q.get(0).member();
        }
    }

    static final class FakeReservationStore implements ReservationStore {
        @Override public Reservation add(long bookId, String member) {
            throw new UnsupportedOperationException("TODO: store it with the next id");
        }

        @Override public List<Reservation> queue(long bookId) {
            throw new UnsupportedOperationException("TODO: this book's reservations, oldest first");
        }

        @Override public boolean cancel(long id) {
            throw new UnsupportedOperationException("TODO: remove it; false if it isn't there");
        }
    }

    // CHECKS
    static List<String> members(ReservationStore store, long bookId) {
        return store.queue(bookId).stream().map(Reservation::member).toList();
    }

    public static void main(String[] args) {
        check("ids count up across all books", List.of(1L, 2L, 3L), () -> {
            FakeReservationStore store = new FakeReservationStore();
            return List.of(store.add(7, "ada").id(), store.add(8, "alan").id(), store.add(7, "grace").id());
        });
        check("each book has its own queue, oldest first", "[ada, grace] [alan] []", () -> {
            FakeReservationStore store = new FakeReservationStore();
            store.add(7, "ada"); store.add(8, "alan"); store.add(7, "grace");
            return members(store, 7) + " " + members(store, 8) + " " + members(store, 99);
        });
        check("cancel removes a reservation once", "true [grace] false false", () -> {
            FakeReservationStore store = new FakeReservationStore();
            Reservation ada = store.add(7, "ada"); store.add(7, "grace");
            boolean first = store.cancel(ada.id());
            return first + " " + members(store, 7) + " " + store.cancel(ada.id()) + " " + store.cancel(42);
        });
        check("ids are never reused after a cancel", 3L, () -> {
            FakeReservationStore store = new FakeReservationStore();
            store.add(7, "ada"); store.add(7, "alan");
            store.cancel(1);
            return store.add(7, "grace").id();
        });
        check("a returned queue is a snapshot", List.of("ada"), () -> {
            FakeReservationStore store = new FakeReservationStore();
            store.add(7, "ada");
            List<Reservation> before = store.queue(7);
            store.add(7, "alan");
            return before.stream().map(Reservation::member).toList();
        });
        check("the hold desk works on the fake", "ada null alan null", () -> {
            FakeReservationStore store = new FakeReservationStore();
            HoldDesk desk = new HoldDesk(store);
            Reservation ada = desk.reserve(7, "ada");
            Reservation again = desk.reserve(7, "ada");
            desk.reserve(7, "alan");
            String first = desk.nextInLine(7);
            store.cancel(ada.id());
            return first + " " + again + " " + desk.nextInLine(7) + " " + desk.nextInLine(8);
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
