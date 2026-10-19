// Examples: 1234 -> 4321, 1200 -> 21, 7 -> 7, 0 -> 0
// Plan:
//   reversed starts at 0
//   while there are digits left:
//     take the last digit, append it to reversed (reversed * 10 + digit)
//     drop the last digit
public class Reverse {
    static int reverseDigits(int number) {
        int n = number;
        int reversed = 0;
        while (n > 0) {
            reversed = reversed * 10 + n % 10;
            n /= 10;
        }
        return reversed;
    }

    public static void main(String[] args) {
        System.out.println(reverseDigits(1234));
        System.out.println(reverseDigits(1200));
        System.out.println(reverseDigits(7));
        System.out.println(reverseDigits(0));
    }
}
