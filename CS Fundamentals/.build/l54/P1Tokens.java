public class P1Tokens {
    public static void main(String[] args) {
        String src = """
            let width = 12;   // a comment
            let area = width * (width - 2);
            area / 4""";
        new Lexer(src).tokens().forEach(System.out::println);

        for (String bad : new String[] {"let x = 3 # 4", "99999999999 + 1", "a//b\n/ c", "letter + let1"}) {
            String shown = bad.replace("\n", "\\n");
            try {
                var texts = new Lexer(bad).tokens().stream()
                        .map(t -> t.type() == T.EOF ? "EOF" : t.type() + " " + t.text()).toList();
                System.out.println(shown + " -> " + texts);
            } catch (CalcError e) {
                System.out.println(shown + " -> CalcError: " + e.getMessage());
            }
        }
    }
}
