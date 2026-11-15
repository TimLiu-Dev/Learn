import java.util.HashSet;
import java.util.List;
import java.util.Objects;

public class Practice {
    // Thin wrappers: old callers keep working. An unknown code can't borrow,
    // but still pays the basic rate. That's what the old code did, so we keep it.
    static int loanLimit(String code) {
        Tier tier = Tier.parse(code);
        return tier == null ? 0 : tier.loanLimit();
    }

    static int lateFeeCents(String code, int daysLate) {
        Tier tier = Tier.parse(code);
        return (tier == null ? Basic.INSTANCE : tier).lateFeeCents(daysLate);
    }

    static final int CENTS_PER_DAY = 25;

    interface Tier {
        int loanLimit();
        int lateFeeCents(int daysLate);

        static Tier parse(String code) {
            return switch (code) {
                case "basic" -> Basic.INSTANCE;
                case "student" -> Student.INSTANCE;
                case "premium" -> Premium.INSTANCE;
                default -> null;
            };
        }
    }

    static final class Basic implements Tier {
        static final Basic INSTANCE = new Basic();
        public int loanLimit() { return 3; }
        public int lateFeeCents(int daysLate) { return Math.max(0, daysLate) * CENTS_PER_DAY; }
    }

    static final class Student implements Tier {
        static final Student INSTANCE = new Student();
        static final int GRACE_DAYS = 3;
        public int loanLimit() { return 5; }
        public int lateFeeCents(int daysLate) { return Math.max(0, daysLate - GRACE_DAYS) * CENTS_PER_DAY; }
    }

    static final class Premium implements Tier {
        static final Premium INSTANCE = new Premium();
        static final int DISCOUNT_CENTS = 100;
        public int loanLimit() { return 10; }
        public int lateFeeCents(int daysLate) { return Math.max(0, Basic.INSTANCE.lateFeeCents(daysLate) - DISCOUNT_CENTS); }
    }

    // CHECKS
    public static void main(String[] args) {
        List<String> codes = List.of("basic", "student", "premium", "gold");
        check("characterisation: loanLimit unchanged", List.of(3, 5, 10, 0), () -> codes.stream().map(Practice::loanLimit).toList());
        check("characterisation: lateFeeCents unchanged for 0, 2, 5 days",
                List.of(List.of(0, 50, 125), List.of(0, 0, 50), List.of(0, 0, 25), List.of(0, 50, 125)),
                () -> codes.stream().map(c -> List.of(lateFeeCents(c, 0), lateFeeCents(c, 2), lateFeeCents(c, 5))).toList());
        check("each tier knows its loan limit", List.of(3, 5, 10),
                () -> List.of(Tier.parse("basic").loanLimit(), Tier.parse("student").loanLimit(), Tier.parse("premium").loanLimit()));
        check("each tier computes its own late fee (basic 4, student 3, student 4, premium 4, premium 10, basic -2)",
                List.of(100, 0, 25, 0, 150, 0),
                () -> List.of(Tier.parse("basic").lateFeeCents(4), Tier.parse("student").lateFeeCents(3), Tier.parse("student").lateFeeCents(4),
                        Tier.parse("premium").lateFeeCents(4), Tier.parse("premium").lateFeeCents(10), Tier.parse("basic").lateFeeCents(-2)));
        check("an unknown code is not a tier", true, () -> Tier.parse("gold") == null && Tier.parse("Basic") == null);
        check("each tier is its own type", 3,
                () -> new HashSet<>(List.of(Tier.parse("basic").getClass(), Tier.parse("student").getClass(), Tier.parse("premium").getClass())).size());
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
