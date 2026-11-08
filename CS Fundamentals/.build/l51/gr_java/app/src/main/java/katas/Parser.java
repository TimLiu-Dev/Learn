package katas;

// Recursive descent (L18), one method per grammar rule:
//   alt    := concat ('|' concat)*
//   concat := repeat*                       (zero repeats is the empty pattern)
//   repeat := atom ('*' | '+' | '?')?
//   atom   := '(' alt ')' | '.' | '\' char | any other char
public class Parser {
    private final String p;
    private int pos = 0;

    public Parser(String pattern) { this.p = pattern; }

    public Node parse() {
        Node tree = alt();
        if (pos < p.length()) throw fail("unexpected '" + p.charAt(pos) + "'");   // only a stray ')' stops us early
        return tree;
    }

    private Node alt() {
        Node node = concat();
        while (pos < p.length() && p.charAt(pos) == '|') { pos++; node = new Node.Alt(node, concat()); }
        return node;
    }

    private Node concat() {
        Node node = null;
        while (pos < p.length() && p.charAt(pos) != '|' && p.charAt(pos) != ')') {
            Node next = repeat();
            node = node == null ? next : new Node.Cat(node, next);
        }
        return node == null ? new Node.Empty() : node;
    }

    private Node repeat() {
        Node atom = atom();
        if (pos == p.length() || "*+?".indexOf(p.charAt(pos)) < 0) return atom;
        char op = p.charAt(pos++);
        if (pos < p.length() && "*+?".indexOf(p.charAt(pos)) >= 0)
            throw fail("'" + p.charAt(pos) + "' after '" + op + "' is not supported");   // lazy/possessive
        return switch (op) {
            case '*' -> new Node.Star(atom);
            case '+' -> new Node.Plus(atom);
            default -> new Node.Opt(atom);
        };
    }

    private Node atom() {
        char c = p.charAt(pos);
        switch (c) {
            case '(' -> {
                pos++;
                Node inner = alt();
                if (pos == p.length() || p.charAt(pos) != ')') throw fail("missing ')'");
                pos++;
                return inner;
            }
            case '.' -> { pos++; return new Node.AnyChar(); }
            case '\\' -> {
                pos++;
                if (pos == p.length()) throw fail("nothing after '\\'");
                return new Node.Lit(p.charAt(pos++));
            }
            case '*', '+', '?' -> throw fail("nothing to repeat before '" + c + "'");
            case '[', ']', '{', '}', '^', '$' -> throw fail("'" + c + "' is not supported");
            default -> { pos++; return new Node.Lit(c); }
        }
    }

    private IllegalArgumentException fail(String message) {
        return new IllegalArgumentException(message + " at index " + pos + " in \"" + p + "\"");
    }
}
