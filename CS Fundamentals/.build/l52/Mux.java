import java.util.ArrayList;
import java.util.List;

public class Mux {
    static int nand(int a, int b) { return (a == 1 && b == 1) ? 0 : 1; }
    static int not(int a) { return nand(a, a); }
    static int and(int a, int b) { return not(nand(a, b)); }
    static int or(int a, int b) { return nand(not(a), not(b)); }

    // The hardware "if": out = a when sel is 0, b when sel is 1.
    static int mux(int a, int b, int sel) { return or(and(a, not(sel)), and(b, sel)); }

    // 1 when all eight wires are 0. A CPU needs this to decide a "jump if zero".
    static int isZero8(int x) {
        int any = 0;
        for (int i = 0; i < 8; i++) any = or(any, (x >> i) & 1);
        return not(any);
    }

    public static void main(String[] args) {
        int muxWrong = 0;
        for (int a = 0; a <= 1; a++)
            for (int b = 0; b <= 1; b++)
                for (int sel = 0; sel <= 1; sel++)
                    if (mux(a, b, sel) != (sel == 1 ? b : a)) muxWrong++;
        System.out.println("mux: 8 rows, wrong = " + muxWrong);
        List<Integer> zeros = new ArrayList<>();
        for (int x = 0; x <= 255; x++) if (isZero8(x) == 1) zeros.add(x);
        System.out.println("isZero8 is 1 for: " + zeros);
    }
}
