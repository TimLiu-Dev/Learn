public class Inline {
    static void twice(Runnable block) {
        block.run();
        block.run();
    }

    public static void main(String[] args) {
        int[] count = {0};
        twice(() -> count[0]++);
        twice(() -> System.out.println("hi"));
        System.out.println(count[0]);
    }
}
