// How deep can parentheses nest before the recursive-descent parser runs out of stack (L4)?
public class Deep {
    public static void main(String[] args) {
        for (int depth : new int[] {1_000, 3_000, 10_000, 30_000}) {
            String src = "(".repeat(depth) + "1" + ")".repeat(depth);
            try {
                Parser.parse(src);
                System.out.printf("depth %,6d: parsed%n", depth);
            } catch (StackOverflowError e) {
                System.out.printf("depth %,6d: StackOverflowError%n", depth);
            }
        }
    }
}
