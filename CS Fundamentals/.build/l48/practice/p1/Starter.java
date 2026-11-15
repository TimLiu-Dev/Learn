import java.util.List;
import java.util.Objects;

public class Practice {
    interface Formula { boolean apply(boolean p, boolean q); }

    // p → q is false on exactly one row: p true and q false.
    static boolean implies(boolean p, boolean q) {
        throw new UnsupportedOperationException("TODO: write it with ! and ||");
    }

    // True when f and g give the same answer for every one of the four (p, q) pairs.
    static boolean equivalent(Formula f, Formula g) {
        throw new UnsupportedOperationException("TODO: compare f and g on all four rows");
    }

    // True when f is true on every row.
    static boolean alwaysTrue(Formula f) {
        throw new UnsupportedOperationException("TODO: check every row, or reuse equivalent");
    }

    // CHECKS
    public static void main(String[] args) {
        check("implies matches the truth table", List.of(true, false, true, true),
            () -> List.of(implies(true, true), implies(true, false), implies(false, true), implies(false, false)));
        check("the contrapositive is equivalent", true, () -> equivalent((p, q) -> implies(p, q), (p, q) -> implies(!q, !p)));
        check("the converse is not equivalent", false, () -> equivalent((p, q) -> implies(p, q), (p, q) -> implies(q, p)));
        check("De Morgan (and) holds", true, () -> equivalent((a, b) -> !(a && b), (a, b) -> !a || !b));
        check("De Morgan (or) holds", true, () -> equivalent((a, b) -> !(a || b), (a, b) -> !a && !b));
        check("(p && q) -> p is a tautology", true, () -> alwaysTrue((p, q) -> implies(p && q, p)));
        check("p -> q is not a tautology", false, () -> alwaysTrue((p, q) -> implies(p, q)));
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
