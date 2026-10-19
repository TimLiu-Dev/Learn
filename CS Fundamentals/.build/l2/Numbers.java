public class Numbers {
    public static void main(String[] args) {
        System.out.println(Integer.MAX_VALUE);       // 2147483647
        System.out.println(Integer.MAX_VALUE + 1);   // wraps around
        System.out.println(Long.MAX_VALUE);
        System.out.println(-7 / 2);                  // truncates toward zero
        System.out.println(-7 % 2);                  // sign follows the left operand
        System.out.println(Math.floorMod(-7, 2));    // always non-negative for a positive divisor
        System.out.println(Math.abs(Integer.MIN_VALUE)); // there is no +2147483648 int!
        System.out.println(0.1 + 0.2);
        System.out.println(1.0 / 0);                 // double division by zero
        System.out.println(0.0 / 0);
        System.out.println(Math.addExact(Integer.MAX_VALUE, 1)); // throws instead of wrapping
    }
}
