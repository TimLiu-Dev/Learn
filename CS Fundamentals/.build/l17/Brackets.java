import java.util.*;

public class Brackets {
    static boolean isBalanced(String text) {
        Map<Character, Character> pairs = Map.of(')', '(', ']', '[', '}', '{');
        Deque<Character> open = new ArrayDeque<>();
        for (char c : text.toCharArray()) {
            switch (c) {
                case '(', '[', '{' -> open.push(c);           // push every opener
                case ')', ']', '}' -> {
                    Character top = open.poll();              // null if nothing is open
                    if (top == null || !top.equals(pairs.get(c))) return false;  // equals, not != (L5)
                }
                default -> { }                                // ignore everything else
            }
        }
        return open.isEmpty();                                // anything left open is unbalanced
    }

    public static void main(String[] args) {
        for (String s : List.of("{[()]}", "f(a[0], {b})", "([)]", "((", "())", ""))
            System.out.printf("%-14s %s%n", "\"" + s + "\"", isBalanced(s));
    }
}
