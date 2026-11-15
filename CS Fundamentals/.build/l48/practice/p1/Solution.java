import java.util.List;
import java.util.Objects;

public class Practice {
    interface Formula { boolean apply(boolean p, boolean q); }

    // p → q is false on exactly one row: p true and q false.
    static boolean implies(boolean p, boolean q) {
        return !p || q;
    }

    // Two nested loops visit all four (p, q) pairs: that's the whole proof.
    static boolean equivalent(Formula f, Formula g) {
        for (boolean p : new boolean[]{false, true})
            for (boolean q : new boolean[]{false, true})
                if (f.apply(p, q) != g.apply(p, q)) return false;
        return true;
    }

    static boolean alwaysTrue(Formula f) {
        return equivalent(f, (p, q) -> true);
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
