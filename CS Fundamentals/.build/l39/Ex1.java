import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

public class Ex1 {
    enum Tier { CHILD, STUDENT, ADULT }
    record Loan(String title, int daysLate, Tier tier) {}
    interface LateFeePolicy { int feeCents(Loan loan); }

    // (a) one strategy per tier, chosen by a map instead of a switch
    static final Map<Tier, LateFeePolicy> BY_TIER = Map.of(
        Tier.CHILD, loan -> 0,
        Tier.STUDENT, loan -> Math.min(loan.daysLate() * 10, 300),
        Tier.ADULT, loan -> loan.daysLate() * 25);

    static int fee(Loan loan, boolean amnestyWeek) {
        LateFeePolicy policy = amnestyWeek ? l -> 0 : BY_TIER.get(loan.tier());   // a new rule, no other edits
        return policy.feeCents(loan);
    }

    // (b) a listener that removes itself after the first event
    static class Desk {
        private final List<Consumer<String>> listeners = new CopyOnWriteArrayList<>();
        void subscribe(Consumer<String> l) { listeners.add(l); }
        void subscribeOnce(Consumer<String> l) {
            // An anonymous class, not a lambda: inside a lambda, `this` is NOT the lambda (L14)
            listeners.add(new Consumer<>() {
                public void accept(String e) { listeners.remove(this); l.accept(e); }
            });
        }
        void publish(String e) { for (Consumer<String> l : listeners) l.accept(e); }
        int count() { return listeners.size(); }
    }

    public static void main(String[] args) {
        List<Loan> loans = List.of(new Loan("Gruffalo", 9, Tier.CHILD), new Loan("SICP", 40, Tier.STUDENT), new Loan("Dune", 3, Tier.ADULT));
        for (Loan l : loans) System.out.println(l.title() + ": " + fee(l, false) + " (amnesty: " + fee(l, true) + ")");
        Desk desk = new Desk();
        desk.subscribe(e -> System.out.println("always: " + e));
        desk.subscribeOnce(e -> System.out.println("once:   " + e));
        desk.publish("Dune lent");
        desk.publish("Emma lent");
        System.out.println("listeners left: " + desk.count());
    }
}
