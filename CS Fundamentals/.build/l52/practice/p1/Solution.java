import java.util.*;

public class Practice {
    // The only primitive. Every call is counted.
    static int nands = 0;
    static int nand(int a, int b) { nands++; return (a == 1 && b == 1) ? 0 : 1; }

    static int not(int a) { return nand(a, a); }
    static int and(int a, int b) { return not(nand(a, b)); }
    static int or(int a, int b) { return nand(not(a), not(b)); }
    static int xor(int a, int b) { int n = nand(a, b); return nand(nand(a, n), nand(b, n)); }

    record SumCarry(int sum, int carry) {}

    // Adds three bits.
    static SumCarry fullAdder(int a, int b, int carryIn) {
        int s1 = xor(a, b);
        int c1 = and(a, b);
        return new SumCarry(xor(s1, carryIn), or(c1, and(s1, carryIn)));
    }

    // Bits are least significant first. Returns a.size() + 1 bits: the last is the final carry.
    static List<Integer> addBits(List<Integer> a, List<Integer> b) {
        if (a.size() != b.size()) throw new IllegalArgumentException("both numbers need the same width");
        int carry = 0;
        List<Integer> out = new ArrayList<>();
        for (int i = 0; i < a.size(); i++) {
            SumCarry r = fullAdder(a.get(i), b.get(i), carry);
            out.add(r.sum());
            carry = r.carry();          // this column's carry-out is the next column's carry-in
        }
        out.add(carry);                 // keep the carry: it is bit n of the answer
        return out;
    }

    // CHECKS
    public static void main(String[] args) {
        check("the full adder matches a + b + c on all 8 rows", 0, () -> {
            int wrong = 0;
            for (int a = 0; a <= 1; a++) for (int b = 0; b <= 1; b++) for (int c = 0; c <= 1; c++) {
                SumCarry r = fullAdder(a, b, c);
                if (r.sum() != ((a + b + c) & 1) || r.carry() != (a + b + c) >> 1) wrong++;
            }
            return wrong;
        });
        check("the full adder is wired from nand", true, () -> { nands = 0; fullAdder(1, 1, 1); return nands > 0; });
        check("a carry ripples all the way to the top", List.of(0, 0, 0, 0, 1), () -> addBits(List.of(1, 1, 1, 1), List.of(1, 0, 0, 0)));
        check("every pair of 6-bit numbers adds like +", 0, () -> {
            int wrong = 0;
            for (int x = 0; x < 64; x++) for (int y = 0; y < 64; y++) {
                List<Integer> sum = addBits(toBits(x, 6), toBits(y, 6));
                if (sum.size() != 7 || fromBits(sum) != x + y) wrong++;
            }
            return wrong;
        });
        check("random 16-bit numbers add like +", 0, () -> {
            Random rng = new Random(52);
            int wrong = 0;
            for (int i = 0; i < 1000; i++) {
                int x = rng.nextInt(65536), y = rng.nextInt(65536);
                if (fromBits(addBits(toBits(x, 16), toBits(y, 16))) != x + y) wrong++;
            }
            return wrong;
        });
    }

    // Helpers for trying things out: toBits(6, 3) is [0, 1, 1], fromBits([0, 1, 1]) is 6
    static List<Integer> toBits(int x, int width) {
        List<Integer> bits = new ArrayList<>();
        for (int i = 0; i < width; i++) bits.add((x >> i) & 1);
        return bits;
    }

    static int fromBits(List<Integer> bits) {
        int x = 0;
        for (int i = 0; i < bits.size(); i++) x |= bits.get(i) << i;
        return x;
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
