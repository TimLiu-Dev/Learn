import java.util.*;

public class Logic {
    // p -> q ("p implies q") is false only when p is true and q is false.
    static boolean implies(boolean p, boolean q) { return !p || q; }

    static String pad(boolean b) { return String.format("%-6s", b); }

    public static void main(String[] args) {
        boolean[] tf = {true, false};
        System.out.println("p      q      p->q   !q->!p  q->p");
        for (boolean p : tf) for (boolean q : tf) {
            System.out.println(String.join(" ", pad(p), pad(q), pad(implies(p, q)), pad(implies(!q, !p)), pad(implies(q, p))));
        }

        // De Morgan, checked on every possible input: a proof by exhaustion
        boolean deMorgan = true;
        for (boolean a : tf) for (boolean b : tf) {
            deMorgan &= !(a && b) == (!a || !b) && !(a || b) == (!a && !b);
        }
        System.out.println("De Morgan holds for all inputs: " + deMorgan);

        // Quantifiers: "for all" is allMatch, "there exists" is anyMatch
        List<Integer> scores = List.of(72, 88, 95);
        System.out.println("all >= 70: " + scores.stream().allMatch(s -> s >= 70)
                + "   any == 100: " + scores.stream().anyMatch(s -> s == 100));
        System.out.println("not all >= 90 == any < 90: "
                + (!scores.stream().allMatch(s -> s >= 90) == scores.stream().anyMatch(s -> s < 90)));
        System.out.println("empty.allMatch(x > 1000): " + List.<Integer>of().stream().allMatch(x -> x > 1000));

        // Sets: the bulk operations MUTATE the receiver, so copy first
        Set<Integer> a = new LinkedHashSet<>(List.of(1, 2, 3, 4));
        Set<Integer> b = new LinkedHashSet<>(List.of(3, 4, 5));
        Set<Integer> union = new LinkedHashSet<>(a); union.addAll(b);
        Set<Integer> inter = new LinkedHashSet<>(a); inter.retainAll(b);
        Set<Integer> diff = new LinkedHashSet<>(a); diff.removeAll(b);
        System.out.println("A ∪ B = " + union + "   A ∩ B = " + inter + "   A − B = " + diff);
        System.out.println("|A ∪ B| = |A| + |B| − |A ∩ B|: " + union.size() + " = " + a.size() + " + " + b.size() + " − " + inter.size());
    }
}
