package katas;

public class Roman {
    private static final int[] VALUES = {1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1};
    private static final String[] SYMBOLS =
            {"M", "CM", "D", "CD", "C", "XC", "L", "XL", "X", "IX", "V", "IV", "I"};

    public static String toRoman(int number) {
        StringBuilder result = new StringBuilder();
        int n = number;
        for (int i = 0; i < VALUES.length; i++) {
            while (n >= VALUES[i]) {    // take the biggest numeral that still fits
                result.append(SYMBOLS[i]);
                n -= VALUES[i];
            }
        }
        return result.toString();
    }
}
