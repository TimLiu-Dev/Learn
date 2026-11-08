// The gates from Gates.java, with a counter so we can see what an addition costs.
public class Adder {
    static long nands = 0;
    static int nand(int a, int b) { nands++; return (a == 1 && b == 1) ? 0 : 1; }
    static int not(int a) { return nand(a, a); }
    static int and(int a, int b) { return not(nand(a, b)); }
    static int or(int a, int b) { return nand(not(a), not(b)); }
    static int xor(int a, int b) { int n = nand(a, b); return nand(nand(a, n), nand(b, n)); }

    record SumCarry(int sum, int carry) {}

    // Half adder: adds two bits.
    static SumCarry halfAdder(int a, int b) { return new SumCarry(xor(a, b), and(a, b)); }

    // Full adder: adds three bits (two inputs plus the carry coming in).
    static SumCarry fullAdder(int a, int b, int carryIn) {
        SumCarry first = halfAdder(a, b);
        SumCarry second = halfAdder(first.sum(), carryIn);
        return new SumCarry(second.sum(), or(first.carry(), second.carry()));
    }

    static int bit(int x, int i) { return (x >> i) & 1; }   // read wire i of a byte (L48)

    // 8-bit ripple-carry adder: eight full adders, each carry wired into the next.
    // << and | here only place the output wires; all the logic is nand.
    static SumCarry add8(int a, int b, int carryIn) {
        int carry = carryIn;
        int sum = 0;
        for (int i = 0; i < 8; i++) {
            SumCarry r = fullAdder(bit(a, i), bit(b, i), carry);
            sum |= r.sum() << i;
            carry = r.carry();
        }
        return new SumCarry(sum, carry);   // (8-bit sum, carry out of bit 7)
    }
    static SumCarry add8(int a, int b) { return add8(a, b, 0); }

    static int not8(int x) {
        int r = 0;
        for (int i = 0; i < 8; i++) r |= not(bit(x, i)) << i;
        return r;
    }

    // Two's complement (L2): a - b = a + (not b) + 1. The "+ 1" is the carry in.
    static int sub8(int a, int b) { return add8(a, not8(b), 1).sum(); }

    public static void main(String[] args) {
        nands = 0;
        SumCarry r = add8(200, 100);
        System.out.println("200 + 100 -> sum=" + r.sum() + " carry=" + r.carry() + "   (300 - 256 = 44)");
        System.out.println("one 8-bit addition used " + nands + " nand gates");
        System.out.println("100 + 100 -> " + add8(100, 100).sum() + ", as a signed byte: " + (byte) add8(100, 100).sum());
        System.out.println("5 - 7 -> " + sub8(5, 7) + ", as a signed byte: " + (byte) sub8(5, 7));

        int wrongAdd = 0, wrongSub = 0;
        for (int a = 0; a <= 255; a++) {
            for (int b = 0; b <= 255; b++) {
                SumCarry s = add8(a, b);
                if (s.sum() != ((a + b) & 0xFF) || s.carry() != (a + b) >> 8) wrongAdd++;
                if (sub8(a, b) != ((a - b) & 0xFF)) wrongSub++;
            }
        }
        System.out.println("all 65,536 pairs: wrong additions = " + wrongAdd + ", wrong subtractions = " + wrongSub);
    }
}
