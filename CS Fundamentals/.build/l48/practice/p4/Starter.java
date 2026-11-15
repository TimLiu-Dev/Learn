import java.util.List;
import java.util.Objects;

public class Practice {
    // A 64-bit Bloom filter: each item sets 3 of the 64 bits in one long.
    static class TinyBloom {
        private long bits = 0;

        long bits() { return bits; }

        // Three bit positions (0..63), cut from different parts of the hash code.
        List<Integer> positions(String item) {
            int h = item.hashCode();
            return List.of(h & 63, (h >>> 6) & 63, (h >>> 12) & 63);
        }

        void add(String item) {
            throw new UnsupportedOperationException("TODO: set the bit at each position");
        }

        // false: definitely absent. true: maybe present.
        boolean mightContain(String item) {
            throw new UnsupportedOperationException("TODO: every position's bit must be 1");
        }
    }

    // CHECKS
    static final List<String> WORDS = List.of("kotlin", "java", "gradle", "android", "spring", "junit", "heap", "stack",
        "lambda", "sequence", "generic", "record", "sealed", "enum", "thread", "socket");

    static TinyBloom full() {
        TinyBloom f = new TinyBloom();
        for (String w : WORDS) f.add(w);
        return f;
    }

    static String expectedBits(String w) {
        long expected = 0;
        for (int i : new TinyBloom().positions(w)) expected |= 1L << i;
        return Long.toBinaryString(expected);
    }

    static String actualBits(String w) {
        TinyBloom f = new TinyBloom();
        f.add(w);
        return Long.toBinaryString(f.bits());
    }

    public static void main(String[] args) {
        check("an empty filter contains nothing", true, () -> WORDS.stream().noneMatch(new TinyBloom()::mightContain));
        check("every added item is found", true, () -> WORDS.stream().allMatch(full()::mightContain));
        for (String w : List.of("kotlin", "android", "sealed", "sequence"))
            check("add sets exactly the bits of " + w, expectedBits(w), () -> actualBits(w));
        check("a fuller filter gives false positives", true, () -> {
            TinyBloom f = full();
            long falseYes = java.util.stream.IntStream.range(0, 1000).filter(i -> f.mightContain("probe-" + i)).count();
            return falseYes > 0;
        });
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
