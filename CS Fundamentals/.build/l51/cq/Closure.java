import java.util.*;

public class Closure {
    // a?a?aa as an NFA: states 0..4, state 4 accepts.
    // Moves: i -a-> i+1 for i = 0..3. ε-moves: 0 -> 1 and 1 -> 2 (each a? may be skipped).
    static Set<Integer> closure(Set<Integer> states) {
        Set<Integer> seen = new TreeSet<>(states);
        Deque<Integer> todo = new ArrayDeque<>(states);
        while (!todo.isEmpty()) {
            int s = todo.pop();
            if (s < 2 && seen.add(s + 1)) todo.push(s + 1);
        }
        return seen;
    }

    static Set<Integer> readA(Set<Integer> states) {
        Set<Integer> next = new TreeSet<>();
        for (int s : states) if (s < 4) next.add(s + 1);
        return closure(next);
    }

    public static void main(String[] args) {
        Set<Integer> start = closure(Set.of(0));
        System.out.println(start + " -a-> " + readA(start));
    }
}
