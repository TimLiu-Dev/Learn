import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

// Java has no sequence builder: to stop a backtracking search early, the visitor
// returns false ("stop") and every level of the recursion passes that answer back up.
public class FirstPerms {
    static int built = 0;

    // Returns false if the visitor asked to stop.
    static boolean permutations(List<Integer> items, List<Integer> current, boolean[] used,
                                Predicate<List<Integer>> visit) {
        if (current.size() == items.size()) { built++; return visit.test(List.copyOf(current)); }
        for (int i = 0; i < items.size(); i++) {
            if (used[i]) continue;
            used[i] = true; current.add(items.get(i));
            boolean keepGoing = permutations(items, current, used, visit);
            current.removeLast(); used[i] = false;
            if (!keepGoing) return false;                // un-choose first, then pass "stop" up
        }
        return true;
    }

    public static void main(String[] args) {
        List<Integer> items = new ArrayList<>();
        for (int i = 1; i <= 10; i++) items.add(i);
        List<List<Integer>> first3 = new ArrayList<>();
        permutations(items, new ArrayList<>(), new boolean[10], p -> { first3.add(p); return first3.size() < 3; });
        first3.forEach(System.out::println);
        System.out.println("permutations built: " + built);
        List<List<Integer>> found = new ArrayList<>();
        permutations(items, new ArrayList<>(), new boolean[10], p -> {
            if (p.get(0) == 10) { found.add(p); return false; }
            return true;
        });
        System.out.println("first with 10 at the front: " + found.get(0));
        System.out.println("permutations built: " + built);
    }
}
