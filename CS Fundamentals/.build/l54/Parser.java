import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// Calc's recursive-descent parser: one method per grammar rule.
//   program := ( "let" IDENT "=" expr ";" )* expr EOF
//   expr    := term ( ( "+" | "-" ) term )*
//   term    := unary ( ( "*" | "/" | "%" ) unary )*
//   unary   := "-" unary | power
//   power   := atom ( "^" unary )?
//   atom    := NUMBER | IDENT | "(" expr ")"
public class Parser {
    private final List<Token> tokens;
    private int pos = 0;

    public Parser(List<Token> tokens) { this.tokens = tokens; }

    public static Program parse(String src) { return new Parser(new Lexer(src).tokens()).program(); }

    public Program program() {
        var lets = new ArrayList<Program.Let>();
        while (peek().type() == T.LET) {
            advance();
            String name = expect(T.IDENT, "a variable name").text();
            expect(T.EQUAL, "'='");
            Expr value = expr();
            expect(T.SEMI, "';'");
            lets.add(new Program.Let(name, value));
        }
        Expr result = expr();
        expect(T.EOF, "end of input");
        return new Program(lets, result);
    }

    private Expr expr() {
        Expr left = term();
        while (peek().type() == T.PLUS || peek().type() == T.MINUS) {
            Token op = advance();
            left = new Expr.Bin(op.type(), left, term(), op);   // loop, not recursion: left-associative
        }
        return left;
    }

    private Expr term() {
        Expr left = unary();
        while (peek().type() == T.STAR || peek().type() == T.SLASH || peek().type() == T.PERCENT) {
            Token op = advance();
            left = new Expr.Bin(op.type(), left, unary(), op);
        }
        return left;
    }

    private Expr unary() {
        if (peek().type() == T.MINUS) {
            Token op = advance();
            return new Expr.Neg(unary(), op);
        }
        return power();
    }

    private Expr power() {
        Expr base = atom();
        if (peek().type() != T.CARET) return base;
        Token op = advance();
        return new Expr.Bin(T.CARET, base, unary(), op);    // recursion on the right: right-associative
    }

    private Expr atom() {
        Token t = advance();
        return switch (t.type()) {
            case NUMBER -> new Expr.Num(Integer.parseInt(t.text()));
            case IDENT -> new Expr.Var(t.text(), t);
            case LPAREN -> {
                Expr inside = expr();
                expect(T.RPAREN, "')'");
                yield inside;
            }
            default -> throw error(t, "a number, a name or '('");
        };
    }

    private Token peek() { return tokens.get(pos); }

    private Token advance() {
        Token t = tokens.get(pos);
        if (t.type() != T.EOF) pos++;
        return t;
    }

    private Token expect(T type, String what) {
        if (peek().type() == type) return advance();
        throw error(peek(), what);
    }

    private CalcError error(Token t, String what) {
        String found = t.type() == T.EOF ? "end of input" : "'" + t.text() + "'";
        return new CalcError("expected " + what + " at " + t.at() + " but found " + found);
    }

    // Lisp-style printer: every operator wraps its operands, so the tree's shape is visible.
    public static String show(Expr e) {
        return switch (e) {
            case Expr.Num n -> String.valueOf(n.value());
            case Expr.Var v -> v.name();
            case Expr.Neg n -> "(neg " + show(n.operand()) + ")";
            case Expr.Bin b -> "(" + b.at().text() + " " + show(b.left()) + " " + show(b.right()) + ")";
        };
    }

    public static String show(Program p) {
        return Stream.concat(
                p.lets().stream().map(l -> "let " + l.name() + " = " + show(l.value())),
                Stream.of(show(p.result()))).collect(Collectors.joining("\n"));
    }
}
