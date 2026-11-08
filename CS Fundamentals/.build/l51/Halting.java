import java.util.List;
import java.util.function.Predicate;

public class Halting {
    // Programs that count their own steps, so we can stop watching them after a budget.
    static class OutOfFuel extends RuntimeException {
        OutOfFuel() { super(null, null, false, false); }          // no stack trace: it's thrown often
    }
    static class Fuel {
        private long left;
        Fuel(long left) { this.left = left; }
        void tick() { if (--left < 0) throw new OutOfFuel(); }
    }

    @FunctionalInterface interface Program { void run(Fuel fuel); }

    // A decider claims to answer "does p halt?" for EVERY program, and must itself always answer.
    record Decider(String name, Predicate<Program> halts) {}

    static boolean finishesWithin(Program p, long steps) {
        try { p.run(new Fuel(steps)); return true; } catch (OutOfFuel e) { return false; }
    }

    // The diagonal program: ask d about yourself, then do the opposite.
    static Program contrarian(Decider d) {
        return new Program() {
            @Override public void run(Fuel fuel) {
                if (d.halts().test(this)) {
                    while (true) fuel.tick();        // d said "halts", so loop forever
                }                                    // d said "loops", so halt right now
            }
        };
    }

    public static void main(String[] args) {
        List<Decider> deciders = List.of(
            new Decider("always says halts", p -> true),
            new Decider("always says loops", p -> false),
            new Decider("guesses from hashCode", p -> p.hashCode() % 2 == 0),
            new Decider("runs it for 1,000 steps", p -> finishesWithin(p, 1_000)));
        for (Decider d : deciders) {
            Program p = contrarian(d);
            String verdict;
            try { verdict = d.halts().test(p) ? "halts" : "loops"; }
            catch (StackOverflowError e) { verdict = "never answers (StackOverflowError)"; }
            String actual;
            try { actual = finishesWithin(p, 10_000_000) ? "halts" : "still running after 10,000,000 steps"; }
            catch (StackOverflowError e) { actual = "can't even start: it asks d first"; }
            System.out.printf("%-24s says %-35s actually: %s%n", d.name(), verdict, actual);
        }
    }
}
