package katas;

import java.util.function.BooleanSupplier;
import java.util.regex.Pattern;

public class App {
    private static void time(String label, BooleanSupplier block) {
        long t0 = System.nanoTime();
        boolean result = block.getAsBoolean();
        System.out.printf("  %-34s %-5s %9.3f ms%n", label, result, (System.nanoTime() - t0) / 1e6);
    }

    public static void main(String[] args) {
        for (int i = 0; i < 200; i++) new TinyRegex("(a|b)*abb").matches("abababb");   // warm-up
        for (int n : new int[] {24, 26}) {
            String p = "a?".repeat(n) + "a".repeat(n), s = "a".repeat(n);
            System.out.println("a?{" + n + "}a{" + n + "} against a{" + n + "}:");
            time("java.util.regex (backtracking)", () -> Pattern.matches(p, s));
            time("TinyRegex (" + new TinyRegex(p).stateCount() + " NFA states)", () -> new TinyRegex(p).matches(s));
        }
        String p = "(.*a)".repeat(12), s = "a".repeat(28) + "x";
        System.out.println("(.*a) twelve times against a{28}x:");
        time("java.util.regex (backtracking)", () -> Pattern.matches(p, s));
        time("TinyRegex", () -> new TinyRegex(p).matches(s));
        String longInput = "a".repeat(1_000_000) + "b";
        System.out.println("(a+)+b against a million a's then b:");
        time("TinyRegex", () -> new TinyRegex("(a+)+b").matches(longInput));
    }
}
