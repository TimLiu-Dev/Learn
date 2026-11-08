// Decode real ARM64 add/sub instructions (from objdump of clang's output) with masks and shifts.
public class ArmFields {
    static int field(long word, int lo, int width) { return (int) ((word >>> lo) & ((1L << width) - 1)); }

    static String decode(long word) {
        int rd = field(word, 0, 5);
        int rn = field(word, 5, 5);
        int rm = field(word, 16, 5);
        String op = field(word, 30, 1) == 1 ? "sub" : "add";
        String r = field(word, 31, 1) == 1 ? "x" : "w";   // 64-bit or 32-bit registers
        return op + " " + r + rd + ", " + r + rn + ", " + r + rm;
    }

    public static void main(String[] args) {
        long[] words = {0x0b0700a3L, 0x0b000020L, 0x0b080148L, 0x4b0700a3L, 0x8b0700a3L};
        for (long w : words) System.out.printf("%08x  ->  %s%n", w, decode(w));
    }
}
