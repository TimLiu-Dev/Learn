// One primitive gate, nand. Every other gate is built from it and nothing else.
// Bits are ints that are always 0 or 1, like wires.
public class Gates {
    static int nand(int a, int b) { return (a == 1 && b == 1) ? 0 : 1; }

    static int not(int a) { return nand(a, a); }
    static int and(int a, int b) { return not(nand(a, b)); }
    static int or(int a, int b) { return nand(not(a), not(b)); }
    static int xor(int a, int b) {
        int n = nand(a, b);
        return nand(nand(a, n), nand(b, n));   // the classic 4-nand xor
    }

    public static void main(String[] args) {
        System.out.println("a b | nand  not a  and  or  xor");
        int wrong = 0;
        for (int a = 0; a <= 1; a++) {
            for (int b = 0; b <= 1; b++) {
                System.out.println(a + " " + b + " |  " + nand(a, b) + "      " + not(a) + "     "
                        + and(a, b) + "    " + or(a, b) + "   " + xor(a, b));
                // Check each gate against Java's own bitwise operators (L48)
                if (and(a, b) != (a & b) || or(a, b) != (a | b) || xor(a, b) != (a ^ b) || not(a) != (1 - a)) wrong++;
            }
        }
        System.out.println("rows that disagree with Java's & | ^: " + wrong);
    }
}
