import java.math.BigInteger;
import java.util.Random;

public class Div3 {
    // A DFA that reads a binary number one bit at a time and tracks its remainder mod 3.
    // Reading bit b turns value v into 2v + b, so remainder r becomes (2r + b) mod 3.
    enum Rem { R0, R1, R2 }

    static Rem step(Rem q, char bit) {
        return switch (q) {                          // exhaustive: no default needed (L7)
            case R0 -> bit == '0' ? Rem.R0 : Rem.R1;
            case R1 -> bit == '0' ? Rem.R2 : Rem.R0;
            case R2 -> bit == '0' ? Rem.R1 : Rem.R2;
        };
    }

    static boolean divisibleBy3(String bits) {
        Rem q = Rem.R0;                              // the start state
        for (char c : bits.toCharArray()) {
            if (c != '0' && c != '1') throw new IllegalArgumentException("not a bit: " + c);
            q = step(q, c);
        }
        return q == Rem.R0;                          // R0 is the only accepting state
    }

    public static void main(String[] args) {
        for (String s : new String[] {"110", "1001", "111"}) {
            StringBuilder trace = new StringBuilder("R0");
            Rem q = Rem.R0;
            for (char c : s.toCharArray()) { q = step(q, c); trace.append(" -> ").append(q); }
            System.out.println(s + " (" + Integer.parseInt(s, 2) + "): " + trace + "  accept=" + divisibleBy3(s));
        }
        // Check the machine against real arithmetic for every bit string of length 1..16
        int checked = 0, wrong = 0;
        for (int len = 1; len <= 16; len++) {
            for (int v = 0; v < (1 << len); v++) {
                String s = String.format("%" + len + "s", Integer.toBinaryString(v)).replace(' ', '0');
                checked++;
                if (divisibleBy3(s) != (v % 3 == 0)) wrong++;
            }
        }
        System.out.println("checked " + checked + " strings, " + wrong + " disagreements");
        // One variable of memory, however long the input
        Random rnd = new Random(51);
        StringBuilder big = new StringBuilder();
        for (int i = 0; i < 1_000_000; i++) big.append(rnd.nextBoolean() ? '1' : '0');
        System.out.println("1,000,000-bit number: dfa=" + divisibleBy3(big.toString())
            + " BigInteger=" + new BigInteger(big.toString(), 2).mod(BigInteger.valueOf(3)).equals(BigInteger.ZERO));
    }
}
