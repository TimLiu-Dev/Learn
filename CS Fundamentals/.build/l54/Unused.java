import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

// Exercise 3: warn about a `let` whose value is never read. A second walk over the same tree.
public class Unused {
    static List<String> vars(Expr e) {
        return switch (e) {
            case Expr.Num n -> List.of();
            case Expr.Var v -> List.of(v.name());
            case Expr.Neg n -> vars(n.operand());
            case Expr.Bin b -> {
                var all = new ArrayList<>(vars(b.left()));
                all.addAll(vars(b.right()));
                yield all;
            }
        };
    }

    static List<String> unusedLets(Program p) {
        var warnings = new ArrayList<String>();
        var current = new HashMap<String, Integer>();    // name -> index of the let that set it
        var read = new HashSet<Integer>();               // lets whose value somebody read
        for (int i = 0; i < p.lets().size(); i++) {
            var let = p.lets().get(i);
            for (String name : vars(let.value())) if (current.containsKey(name)) read.add(current.get(name));
            Integer old = current.get(let.name());
            if (old != null && !read.contains(old)) {
                warnings.add("let #" + (old + 1) + ": '" + let.name() + "' is overwritten before it's read");
            }
            current.put(let.name(), i);
        }
        for (String name : vars(p.result())) if (current.containsKey(name)) read.add(current.get(name));
        current.forEach((name, i) -> { if (!read.contains(i)) warnings.add("let #" + (i + 1) + ": '" + name + "' is never read"); });
        warnings.sort(null);
        return warnings;
    }

    public static void main(String[] args) {
        for (String src : new String[] {"let x = 1; let y = 2; x", "let x = 1; let x = 2; x",
                                        "let x = 1; let x = x + 1; x", "let a = 1; let b = a; 7"}) {
            var w = unusedLets(Parser.parse(src));
            System.out.println(String.format("%-28s", src) + (w.isEmpty() ? List.of("no warnings") : w));
        }
    }
}
