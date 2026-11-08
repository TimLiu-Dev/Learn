public class P3Eval {
    public static void main(String[] args) {
        for (String src : new String[] {"1 + 2 * 3", "2 ^ 3 ^ 2", "-2 ^ 2", "let x = 3; x * (x + 1)",
                                        "let x = 5; let x = x * x; x - 1", "2147483647 + 1", "7 / 2 + -7 % 2"}) {
            System.out.println(String.format("%-32s= ", src) + Interp.interpret(Parser.parse(src)));
        }
        for (String bad : new String[] {"let a = 1; a + b", "10 / (5 - 5)", "2 ^ -1"}) {
            try {
                Interp.interpret(Parser.parse(bad));
            } catch (CalcError e) {
                System.out.println(String.format("%-32s", bad) + "CalcError: " + e.getMessage());
            }
        }
    }
}
