import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

// A saga: a sequence of local transactions, each with a compensating action that undoes it.
public class Saga {
    static class Inventory {
        int seats, rooms, charged;
        Inventory(int seats, int rooms) { this.seats = seats; this.rooms = rooms; }
    }

    record Step(String name, Predicate<Inventory> action, Consumer<Inventory> compensate) {}

    static boolean runSaga(Inventory inv, List<Step> steps, Consumer<String> afterEach) {
        Deque<Step> done = new ArrayDeque<>();
        for (Step step : steps) {
            if (!step.action().test(inv)) {
                System.out.println("  " + step.name() + ": FAILED");
                while (!done.isEmpty()) {                      // undo in reverse order
                    Step undo = done.removeLast();
                    undo.compensate().accept(inv);
                    System.out.println("  compensate " + undo.name());
                }
                return false;
            }
            done.addLast(step);
            System.out.println("  " + step.name() + ": ok");
            afterEach.accept(step.name());
        }
        return true;
    }

    static List<Step> trip(boolean cardWorks) {
        return List.of(
            new Step("reserve flight", i -> { if (i.seats > 0) { i.seats--; return true; } return false; }, i -> i.seats++),
            new Step("reserve hotel", i -> { if (i.rooms > 0) { i.rooms--; return true; } return false; }, i -> i.rooms++),
            new Step("charge card", i -> { if (cardWorks) { i.charged += 450; return true; } return false; }, i -> i.charged -= 450));
    }

    public static void main(String[] args) {
        System.out.println("== Ana's trip: every step succeeds");
        Inventory inv1 = new Inventory(1, 1);
        boolean ok1 = runSaga(inv1, trip(true), name -> {});
        System.out.println("  committed: " + ok1 + ", seats=" + inv1.seats + " rooms=" + inv1.rooms + " charged=" + inv1.charged);

        System.out.println("== Ben's trip: the card is declined, so earlier steps are compensated");
        Inventory inv2 = new Inventory(1, 1);
        String[] seenByCleo = {""};
        boolean ok2 = runSaga(inv2, trip(false), name -> {
            if (name.equals("reserve hotel")) seenByCleo[0] = inv2.rooms == 0 ? "sold out" : "available";
        });
        System.out.println("  committed: " + ok2 + ", seats=" + inv2.seats + " rooms=" + inv2.rooms + " charged=" + inv2.charged);
        System.out.println("  meanwhile Cleo searched hotels between Ben's steps and saw: " + seenByCleo[0]);
        System.out.println("  (no isolation: she saw a reservation that was later undone)");
    }
}
