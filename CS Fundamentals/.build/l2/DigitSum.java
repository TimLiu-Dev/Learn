public class DigitSum {
    static int digitSum(int number) {
        // 1. Use the absolute value, so the sign doesn't matter
        int n = Math.abs(number);
        int sum = 0;
        // 2. Peel off the last digit until nothing is left
        while (n > 0) {
            sum += n % 10;   // last digit
            n /= 10;         // drop the last digit
        }
        return sum;
    }

    public static void main(String[] args) {
        System.out.println(digitSum(123));               // expect 6
        System.out.println(digitSum(0));                 // expect 0
        System.out.println(digitSum(-45));               // expect 9
        System.out.println(digitSum(Integer.MIN_VALUE)); // expect 47 ... ?
    }
}
