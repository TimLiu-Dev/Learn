public class Ex1 {
    static int sumDigits(int n) {
        return n < 10 ? n : n % 10 + sumDigits(n / 10);
    }

    static boolean isPalindrome(String s) {
        if (s.length() <= 1) return true;
        return s.charAt(0) == s.charAt(s.length() - 1)
                && isPalindrome(s.substring(1, s.length() - 1));
    }

    static int powCalls = 0;

    // x^n = (x^(n/2))^2, times x again if n is odd: halving, like binary search
    static long fastPow(long x, int n) {
        powCalls++;
        if (n == 0) return 1;
        long half = fastPow(x, n / 2);
        return n % 2 == 0 ? half * half : half * half * x;
    }

    public static void main(String[] args) {
        System.out.println(sumDigits(1234) + " " + sumDigits(7));
        System.out.println(isPalindrome("racecar") + " " + isPalindrome("rocket") + " " + isPalindrome(""));
        System.out.println("2^62 = " + fastPow(2, 62) + " in " + powCalls + " calls");
    }
}
