import java.nio.charset.StandardCharsets;

// A Bloom filter: m bits, k hash positions per key. "No" is always right; "maybe" can be wrong.
public class Bloom {
    final int m, k;
    private final long[] bits;

    Bloom(int m, int k) { this.m = m; this.k = k; this.bits = new long[(m + 63) / 64]; }

    void add(String key) {
        for (int p : positions(key)) bits[p >>> 6] |= 1L << p;
    }

    boolean mightContain(String key) {
        for (int p : positions(key)) if ((bits[p >>> 6] & (1L << p)) == 0) return false;
        return true;
    }

    // Two hashes simulate k of them: g_i = h1 + i*h2 (Kirsch & Mitzenmacher).
    private int[] positions(String key) {
        long h = hash64(key);
        int h1 = (int) h, h2 = (int) (h >>> 32);
        int[] out = new int[k];
        for (int i = 0; i < k; i++) out[i] = Math.floorMod(h1 + i * h2, m);
        return out;
    }

    static int optimalK(int m, int n) { return Math.max(1, (int) Math.round(Math.log(2) * m / n)); }
    static double predicted(int m, int n, int k) { return Math.pow(1 - Math.exp(-(double) k * n / m), k); }

    // FNV-1a over the UTF-8 bytes, then MurmurHash3's 64-bit finaliser to mix the bits well.
    static long hash64(String s) {
        long h = 0xcbf29ce484222325L;
        for (byte b : s.getBytes(StandardCharsets.UTF_8)) { h ^= b & 0xff; h *= 0x100000001b3L; }
        h ^= h >>> 33; h *= 0xff51afd7ed558ccdL;
        h ^= h >>> 33; h *= 0xc4ceb9fe1a85ec53L;
        return h ^ (h >>> 33);
    }
}
