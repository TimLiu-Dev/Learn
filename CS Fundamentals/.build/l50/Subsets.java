import java.util.ArrayList;
import java.util.List;

// Backtracking = choose, explore, un-choose. One growing partial solution, shared by every call.
public class Subsets {
    static List<String> subsets(List<Character> items) {
        List<String> out = new ArrayList<>();
        goSubsets(items, 0, new StringBuilder(), out);   // Java has no local functions: pass the state along
        return out;
    }

    private static void goSubsets(List<Character> items, int i, StringBuilder chosen, List<String> out) {
        if (i == items.size()) { out.add("{" + chosen + "}"); return; }
        goSubsets(items, i + 1, chosen, out);            // branch 1: leave items[i] out
        chosen.append(items.get(i));                     // choose
        goSubsets(items, i + 1, chosen, out);            // branch 2: put it in
        chosen.setLength(chosen.length() - 1);           // un-choose: undo before returning
    }

    static List<List<Integer>> permutations(List<Integer> items) {
        List<List<Integer>> out = new ArrayList<>();
        goPerms(items, new ArrayList<>(), new boolean[items.size()], out);
        return out;
    }

    private static void goPerms(List<Integer> items, List<Integer> current, boolean[] used, List<List<Integer>> out) {
        if (current.size() == items.size()) { out.add(List.copyOf(current)); return; }   // copy! current keeps changing
        for (int i = 0; i < items.size(); i++) {
            if (used[i]) continue;
            used[i] = true; current.add(items.get(i));           // choose
            goPerms(items, current, used, out);                  // explore
            current.removeLast(); used[i] = false;               // un-choose
        }
    }

    public static void main(String[] args) {
        System.out.println("subsets of abc: " + subsets(List.of('a', 'b', 'c')));
        System.out.println("permutations of 123: " + permutations(List.of(1, 2, 3)));
        for (int n : new int[] {5, 10, 15, 20}) {
            List<Character> xs = new ArrayList<>();
            for (int i = 0; i < n; i++) xs.add((char) ('a' + i));
            System.out.println("n=" + n + ": " + subsets(xs).size() + " subsets");
        }
        for (int n = 6; n <= 9; n++) {
            List<Integer> xs = new ArrayList<>();
            for (int i = 0; i < n; i++) xs.add(i);
            System.out.println("n=" + n + ": " + permutations(xs).size() + " permutations");
        }
    }
}
