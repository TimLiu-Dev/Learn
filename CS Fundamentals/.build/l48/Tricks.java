import java.util.*;

public class Tricks {
    // n & (n - 1) clears the lowest 1-bit, so it's 0 only when n had exactly one 1-bit
    static boolean isPowerOfTwo(int n) { return n > 0 && (n & (n - 1)) == 0; }

    public static void main(String[] args) {
        for (int n : new int[]{1, 6, 64, 96, 1024}) {
            System.out.printf("%5d  %-12s  power of 2: %-5s  ones: %d  highest: %4d  lowest: %d%n",
                    n, Integer.toBinaryString(n), isPowerOfTwo(n), Integer.bitCount(n), Integer.highestOneBit(n), n & -n);
        }

        // For a power-of-two table size, "h & (size - 1)" is floorMod(h, size): the low bits ARE the remainder
        int size = 16;
        List<Integer> keys = new ArrayList<>(), masked = new ArrayList<>(), spread = new ArrayList<>();
        for (int i = 1; i <= 8; i++) keys.add(i << 16);          // 65536, 131072, ...: differ only in high bits
        for (int k : keys) {
            int h = Integer.hashCode(k);
            masked.add(h & (size - 1));
            spread.add((h ^ (h >>> 16)) & (size - 1));
        }
        System.out.println("keys:            " + keys);
        System.out.println("h & 15:          " + masked);
        System.out.println("spread & 15:     " + spread);
        boolean same = true;
        for (int x = -1000; x <= 1000; x++) same &= (x & (size - 1)) == Math.floorMod(x, size);
        System.out.println("& == floorMod?   " + same);
    }
}
