public class LyingComment {
    static final int CENTS_PER_DAY = 25;

    /** Returns the late fee in dollars. */           // true in version 1...
    static int lateFee(int daysLate) {
        return daysLate * CENTS_PER_DAY;              // ...version 2 switched to cents
    }

    public static void main(String[] args) {
        // The caller trusted the comment:
        System.out.println("Late fee for 4 days: $" + lateFee(4));
    }
}
