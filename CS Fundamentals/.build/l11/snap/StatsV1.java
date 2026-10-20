package katas;

public class Stats {
    public static double average(int... xs) {
        if (xs.length == 0) return 0.0;
        int sum = 0;
        for (int x : xs) sum += x;
        return (double) sum / xs.length;
    }

    public static boolean isLeap(int year) {
        return (year % 4 == 0 && year % 100 != 0) || year % 400 == 0;
    }
}
