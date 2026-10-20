package katas;

import java.util.Map;

public class Brackets {
    private static final Map<Character, Character> PAIRS = Map.of(')', '(', ']', '[', '}', '{');

    public static boolean isBalanced(String text) {
        MyStack<Character> open = new MyStack<>();
        for (char c : text.toCharArray()) {
            if (PAIRS.containsValue(c)) open.push(c);
            else if (PAIRS.containsKey(c)) {
                if (open.isEmpty() || !open.pop().equals(PAIRS.get(c))) return false;
            }
        }
        return open.isEmpty();
    }
}
