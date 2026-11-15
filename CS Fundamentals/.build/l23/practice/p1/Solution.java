import java.util.ArrayDeque;
import java.util.List;
import java.util.Objects;

public class Practice {
    // One instruction per string: "iconst 5", "iload 0", "istore 2", "iadd", "isub", "imul", "ireturn".
    static int execute(List<String> code, int... args) {
        int[] locals = new int[8];
        System.arraycopy(args, 0, locals, 0, args.length);   // parameters live in slots 0, 1, 2, …
        ArrayDeque<Integer> stack = new ArrayDeque<>();      // the operand stack: push/pop at the head
        for (String line : code) {
            String[] parts = line.split(" ");
            switch (parts[0]) {
                case "iconst" -> stack.push(Integer.parseInt(parts[1]));
                case "iload" -> stack.push(locals[Integer.parseInt(parts[1])]);
                case "istore" -> locals[Integer.parseInt(parts[1])] = stack.pop();
                case "iadd", "isub", "imul" -> {
                    int right = stack.pop();                 // pushed last, so popped first
                    int left = stack.pop();
                    stack.push(switch (parts[0]) {
                        case "iadd" -> left + right;
                        case "isub" -> left - right;
                        default -> left * right;
                    });
                }
                case "ireturn" -> { return stack.pop(); }
                default -> throw new IllegalStateException("unknown instruction: " + line);
            }
        }
        throw new IllegalStateException("program ended without ireturn");
    }

    // CHECKS
    public static void main(String[] args) {
        check("add(2, 3) is 5", 5, () -> execute(List.of("iload 0", "iload 1", "iadd", "ireturn"), 2, 3));
        check("mul3(2, 3, 4) is 24", 24, () -> execute(List.of("iload 0", "iload 1", "imul", "iload 2", "imul", "ireturn"), 2, 3, 4));
        check("isub takes the first-pushed value minus the last-pushed", 7, () -> execute(List.of("iload 0", "iload 1", "isub", "ireturn"), 10, 3));
        check("istore and iload round-trip through a local slot", 41,
              () -> execute(List.of("iload 0", "iload 1", "imul", "istore 2", "iload 2", "iconst 1", "isub", "ireturn"), 6, 7));
        check("ints wrap around like the JVM's", Integer.MIN_VALUE, () -> execute(List.of("iload 0", "iconst 1", "iadd", "ireturn"), Integer.MAX_VALUE));
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
