public class Bits {
    static String bin(int x, int width) {
        String s = "0".repeat(32) + Integer.toBinaryString(x);
        return s.substring(s.length() - width);
    }
    static String bin(int x) { return bin(x, 8); }

    public static void main(String[] args) {
        int a = 0b1100;   // 12
        int b = 0b1010;   // 10
        System.out.println("a     = " + bin(a));
        System.out.println("b     = " + bin(b));
        System.out.println("a & b = " + bin(a & b) + "  (" + (a & b) + ")");
        System.out.println("a | b = " + bin(a | b) + "  (" + (a | b) + ")");
        System.out.println("a ^ b = " + bin(a ^ b) + "  (" + (a ^ b) + ")");
        System.out.println("a << 2 = " + bin(a << 2) + "  (" + (a << 2) + ")   a >> 2 = " + bin(a >> 2) + "  (" + (a >> 2) + ")");
        System.out.println("~a    = " + ~a + "   (all 32 bits flipped: -a - 1)");

        int n = -16;
        System.out.println("n       = " + bin(n, 32));
        System.out.println("n >> 2  = " + bin(n >> 2, 32) + "  (" + (n >> 2) + ")");
        System.out.println("n >>> 2 = " + bin(n >>> 2, 32) + "  (" + (n >>> 2) + ")");
        System.out.println("1 << 31 = " + (1 << 31) + "   1 << 32 = " + (1 << 32) + "   1L << 32 = " + (1L << 32));
        System.out.println("010 = " + 010 + "   (a leading 0 means octal in Java)");
    }
}
