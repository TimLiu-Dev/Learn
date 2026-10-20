package katas;

public class Stats {
    public static double average(int... xs) {
        if (xs.length == 0) return 0.0;
        long sum = 0;                // long: two big ints can't overflow it
        for (int x : xs) sum += x;
        return (double) sum / xs.length;
    }

    public static boolean isLeap(int year) {
        return (year % 4 == 0 && year % 100 != 0) || year % 400 == 0;
    }
}
