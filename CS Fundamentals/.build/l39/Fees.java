import java.util.*;

public class Fees {
    record Loan(String title, int daysLate, boolean student) {}

    // The strategy interface: one job, many interchangeable implementations
    interface LateFeePolicy {
        int feeCents(Loan loan);
    }

    static class FlatFee implements LateFeePolicy {
        public int feeCents(Loan loan) { return loan.daysLate() > 0 ? 500 : 0; }
    }

    static class PerDayFee implements LateFeePolicy {
        private final int centsPerDay;
        PerDayFee(int centsPerDay) { this.centsPerDay = centsPerDay; }
        public int feeCents(Loan loan) { return loan.daysLate() * centsPerDay; }
    }

    static class CappedFee implements LateFeePolicy {
        private final LateFeePolicy inner;
        private final int capCents;
        CappedFee(LateFeePolicy inner, int capCents) { this.inner = inner; this.capCents = capCents; }
        public int feeCents(Loan loan) { return Math.min(inner.feeCents(loan), capCents); }
    }

    // The context: it knows WHEN to charge; the strategy knows HOW MUCH
    static class Library {
        private final LateFeePolicy policy;
        Library(LateFeePolicy policy) { this.policy = policy; }
        int totalFees(List<Loan> loans) {
            int total = 0;
            for (Loan l : loans) total += policy.feeCents(l);
            return total;
        }
    }

    public static void main(String[] args) {
        List<Loan> loans = List.of(new Loan("Dune", 3, false), new Loan("Emma", 6, true), new Loan("Ulysses", 40, false));
        Map<String, LateFeePolicy> policies = new LinkedHashMap<>();
        policies.put("flat", new FlatFee());
        policies.put("per-day", new PerDayFee(25));
        policies.put("capped", new CappedFee(new PerDayFee(25), 600));
        // Since Java 8 a lambda can BE the strategy: LateFeePolicy has one abstract method (L14)
        policies.put("students free", loan -> loan.student() ? 0 : loan.daysLate() * 25);
        policies.forEach((name, p) -> System.out.println(name + ": " + new Library(p).totalFees(loans) + " cents"));

        // The JDK's most famous strategy: a Comparator passed to sort
        List<Loan> sorted = new ArrayList<>(loans);
        sorted.sort(Comparator.comparingInt(Loan::daysLate).reversed());
        System.out.println("most late first: " + sorted.stream().map(Loan::title).toList());
    }
}
