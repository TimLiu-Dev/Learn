import java.util.ArrayList;
import java.util.List;

// The classic backtracking bug: storing the shared, still-changing list instead of a copy.
public class NoCopy {
    static List<List<Integer>> out = new ArrayList<>();
    static List<Integer> current = new ArrayList<>();
    static boolean[] used = new boolean[3];

    static void go() {
        if (current.size() == 3) { out.add(current); return; }   // BUG: should be List.copyOf(current)
        for (int i = 0; i < 3; i++) {
            if (used[i]) continue;
            used[i] = true; current.add(i + 1);
            go();
            current.removeLast(); used[i] = false;
        }
    }

    public static void main(String[] args) {
        go();
        System.out.println(out);
        System.out.println("all the same object: " + out.stream().allMatch(l -> l == current));
    }
}
