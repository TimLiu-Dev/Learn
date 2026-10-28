// Three ways to turn a long URL into a short code.
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class CodeGen {
    static final String ALPHABET = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    static String encode(long n) {
        if (n < 0) throw new IllegalArgumentException("negative: " + n);
        if (n == 0) return "0";
        var sb = new StringBuilder();
        while (n > 0) {
            sb.append(ALPHABET.charAt((int) (n % 62)));
            n /= 62;
        }
        return sb.reverse().toString();
    }

    static long decode(String code) {
        long acc = 0;
        for (char c : code.toCharArray()) {
            int digit = ALPHABET.indexOf(c);
            if (digit < 0) throw new IllegalArgumentException("not base62: '" + c + "'");
            acc = acc * 62 + digit;
        }
        return acc;
    }

    // 1. Counter: the database hands out 1, 2, 3, ...; the code is just the id in base 62.
    static String counterCode(long id) { return encode(id); }

    // 2. Hash and truncate: same URL, same code, but different URLs can collide.
    static String hashCode7(String url) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256").digest(url.getBytes(StandardCharsets.UTF_8));
            long n = 0;
            for (int i = 0; i < 8; i++) n = (n << 8) | (digest[i] & 0xff);
            String code = encode(Math.floorMod(n, 3_521_614_606_208L));     // 62^7
            return "0".repeat(7 - code.length()) + code;
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }

    // 3. Random: unguessable; the caller must retry if the code is already taken.
    static final SecureRandom RNG = new SecureRandom();
    static String randomCode(int length) {
        char[] cs = new char[length];
        for (int i = 0; i < length; i++) cs[i] = ALPHABET.charAt(RNG.nextInt(62));
        return new String(cs);
    }

    public static void main(String[] args) {
        for (long n : new long[]{0, 61, 62, 3843, 3844, 125_000_000_000L, Long.MAX_VALUE}) {
            String code = encode(n);
            if (decode(code) != n) throw new AssertionError("round trip failed for " + n);
            System.out.printf("%-20d -> %s%n", n, code);
        }
        List<String> seq = new ArrayList<>();
        for (long id = 1000; id <= 1004; id++) seq.add(counterCode(id));
        System.out.println("counter ids 1000..1004 -> " + seq);
        System.out.println("hash of /a twice       -> " + hashCode7("https://example.com/a") + " " + hashCode7("https://example.com/a"));
        System.out.println("hash of /b             -> " + hashCode7("https://example.com/b"));
        System.out.println("random x3              -> " + List.of(randomCode(7), randomCode(7), randomCode(7)));
    }
}
