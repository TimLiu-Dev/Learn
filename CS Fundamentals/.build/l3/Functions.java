public class Functions {
    static String greet(String name) {
        return greet(name, "Hello", "!");
    }

    static String greet(String name, String greeting) {
        return greet(name, greeting, "!");
    }

    static String greet(String name, String greeting, String punctuation) {
        return greeting + ", " + name + punctuation;
    }

    static double average(int... xs) {
        if (xs.length == 0) return 0.0;
        int sum = 0;
        for (int x : xs) sum += x;
        return (double) sum / xs.length;
    }

    static void logLine(String message) {
        System.out.println("[log] " + message);
    }

    public static void main(String[] args) {
        System.out.println(greet("Ada"));
        System.out.println(greet("Ada", "Hi"));
        System.out.println(greet("Ada", "Hello", "?"));   // must repeat the default
        System.out.println(greet("Linus", "Welcome"));
        System.out.println(average(3, 4, 5));
        System.out.println(average());
        int[] nums = {1, 2};
        System.out.println(average(nums));
        logLine("done");
    }
}
