import java.util.*;

public class Ex3 {
    static int evalPostfix(String expr) {
        Deque<Integer> stack = new ArrayDeque<>();
        for (String token : expr.trim().split("\\s+")) {
            if (token.matches("-?\\d+")) { stack.push(Integer.parseInt(token)); continue; }
            if (stack.size() < 2) throw new IllegalArgumentException("not enough operands for '" + token + "'");
            int right = stack.pop();                // popped first = the RIGHT operand
            int left = stack.pop();
            stack.push(switch (token) {
                case "+" -> left + right;
                case "-" -> left - right;
                case "*" -> left * right;
                case "/" -> left / right;
                default -> throw new IllegalArgumentException("unknown token '" + token + "'");
            });
        }
        if (stack.size() != 1) throw new IllegalArgumentException("expected one result, stack is " + stack);
        return stack.pop();
    }

    public static void main(String[] args) {
        for (String e : List.of("3 4 +", "3 4 + 2 *", "10 2 8 * + 3 -", "7 2 -", "1 +", "1 2", "2 3 ^")) {
            try { System.out.println(e + " = " + evalPostfix(e)); }
            catch (IllegalArgumentException ex) { System.out.println(e + " -> error: " + ex.getMessage()); }
        }
    }
}
