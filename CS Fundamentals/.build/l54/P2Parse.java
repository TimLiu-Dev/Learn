public class P2Parse {
    public static void main(String[] args) {
        for (String src : new String[] {"1 + 2 * 3", "(1 + 2) * 3", "8 - 3 - 2", "2 ^ 3 ^ 2", "-2 ^ 2",
                                        "let x = 3; x * (x + 1)"}) {
            System.out.println(String.format("%-24s", src) + Parser.show(Parser.parse(src)).replace("\n", "; "));
        }
        System.out.println(Parser.parse("8 - 3 - 2").result());
        for (String bad : new String[] {"(1 + 2", "1 + * 2", "let = 4; 1", "1 2"}) {
            try {
                Parser.parse(bad);
            } catch (CalcError e) {
                System.out.println(String.format("%-12s", bad) + "CalcError: " + e.getMessage());
            }
        }
    }
}
