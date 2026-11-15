import java.util.HashSet;
import java.util.List;
import java.util.Objects;

public class Practice {
    // The membership code is switched on twice. A new tier means editing both.
    static int loanLimit(String code) {
        if (code.equals("basic")) return 3;
        else if (code.equals("student")) return 5;
        else if (code.equals("premium")) return 10;
        else return 0;
    }

    static int lateFeeCents(String code, int daysLate) {
        if (daysLate <= 0) return 0;
        if (code.equals("student")) {
            if (daysLate <= 3) return 0;          // three days' grace
            return (daysLate - 3) * 25;
        } else if (code.equals("premium")) {
            int fee = daysLate * 25 - 100;        // 100 cents off, never below zero
            if (fee < 0) return 0;
            return fee;
        } else {
            return daysLate * 25;
        }
    }

    interface Tier {
        int loanLimit();
        int lateFeeCents(int daysLate);

        static Tier parse(String code) {
            throw new UnsupportedOperationException("TODO: one class per tier; null for an unknown code");
        }
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
