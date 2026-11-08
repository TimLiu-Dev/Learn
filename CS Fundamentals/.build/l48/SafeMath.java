import java.util.*;

public class SafeMath {
    static int dayAfter(int day, int delta) { return Math.floorMod(day + delta, 7); }   // 0 = Monday

    static int bucket(Object key, int buckets) { return Math.floorMod(key.hashCode(), buckets); }

    static int digitCount(long n) {
        if (n < 0) throw new IllegalArgumentException("n must be >= 0");
        int count = 1;
        while (n >= 10) { n /= 10; count++; }
        return count;
    }

    static int digitCountByLog(long n) { return (int) Math.floor(Math.log10(n)) + 1; }   // the tempting version

    public static void main(String[] args) {
        // Brute force: step one day at a time, forwards or backwards
        for (int day = 0; day <= 6; day++) for (int delta = -30; delta <= 30; delta++) {
            int d = day;
            for (int s = 0; s < Math.abs(delta); s++) d = delta > 0 ? (d + 1) % 7 : (d + 6) % 7;
            if (dayAfter(day, delta) != d) throw new IllegalStateException("dayAfter(" + day + ", " + delta + ")");
        }
        System.out.println("dayAfter matches stepping for 427 cases");

        List<String> words = List.of("android", "polygenelubricants", "kotlin", "java", "spring boot");
        List<Integer> buckets = new ArrayList<>();
        for (String w : words) buckets.add(bucket(w, 10));
        System.out.println("buckets: " + buckets);

        for (long n = 0; n <= 1_000_000; n++) {
            if (digitCount(n) != Long.toString(n).length()) throw new IllegalStateException("digitCount(" + n + ")");
        }
        System.out.println("digitCount matches toString().length for 0..1000000");
        long nines = 9;
        for (int k = 1; k <= 18; k++) {
            if (digitCountByLog(nines) != k) { System.out.println("log10 version first fails at " + nines + ": says " + digitCountByLog(nines)); break; }
            nines = nines * 10 + 9;
        }
    }
}
