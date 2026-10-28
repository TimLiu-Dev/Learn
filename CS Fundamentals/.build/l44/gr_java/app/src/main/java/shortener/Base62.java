package shortener;

import java.security.SecureRandom;

public final class Base62 {
    private static final String ALPHABET = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final SecureRandom RNG = new SecureRandom();

    private Base62() {}

    public static String encode(long n) {
        if (n < 0) throw new IllegalArgumentException("negative: " + n);
        if (n == 0) return "0";
        var sb = new StringBuilder();
        while (n > 0) {
            sb.append(ALPHABET.charAt((int) (n % 62)));
            n /= 62;
        }
        return sb.reverse().toString();
    }

    public static long decode(String code) {
        long acc = 0;
        for (char c : code.toCharArray()) {
            int digit = ALPHABET.indexOf(c);
            if (digit < 0) throw new IllegalArgumentException("not base62: '" + c + "'");
            acc = acc * 62 + digit;
        }
        return acc;
    }

    public static String random(int length) {
        char[] cs = new char[length];
        for (int i = 0; i < length; i++) cs[i] = ALPHABET.charAt(RNG.nextInt(62));
        return new String(cs);
    }
}
