// How soon does "hash the URL, keep the first k base62 characters" collide?
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HashSet;

public class Collisions {
    static long truncatedHash(String url, long space) throws Exception {
        byte[] d = MessageDigest.getInstance("SHA-256").digest(url.getBytes(StandardCharsets.UTF_8));
        long n = 0;
        for (int i = 0; i < 8; i++) n = (n << 8) | (d[i] & 0xff);
        return Math.floorMod(n, space);
    }

    // Insert distinct URLs until two of them get the same truncated code.
    static int firstCollision(String prefix, long space) throws Exception {
        var seen = new HashSet<Long>();
        int i = 0;
        while (seen.add(truncatedHash("https://example.com/" + prefix + "/" + i, space))) i++;
        return i + 1;
    }

    public static void main(String[] args) throws Exception {
        for (int chars : new int[]{5, 7}) {
            long space = (long) Math.pow(62, chars);
            int trials = chars == 5 ? 20 : 5;
            long sum = 0;
            var each = new StringBuilder();
            for (int t = 1; t <= trials; t++) {
                int c = firstCollision("t" + t, space);
                sum += c;
                each.append(t > 1 ? ", " : "").append("%,d".formatted(c));
            }
            System.out.printf("%d chars: space %,d, birthday estimate %,.0f, measured mean %,d over %d trials%n",
                    chars, space, Math.sqrt(Math.PI * space / 2), sum / trials, trials);
            if (chars == 7) System.out.println("  each trial: " + each);
        }
        double n = 6e9;                       // links after 5 years (Estimates)
        double space7 = Math.pow(62, 7);
        System.out.printf("6.0 G links in 7 chars: P(any collision) = %.6f, expected colliding pairs ≈ %,.0f%n",
                1 - Math.exp(-n * n / (2 * space7)), n * n / (2 * space7));
    }
}
