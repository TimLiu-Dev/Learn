package katas;

import java.util.ArrayList;
import java.util.List;

// Calc's scanner: source text -> a list of tokens, each remembering where it came from.
public class Lexer {
    private final String src;
    private int i = 0, line = 1, col = 1;

    public Lexer(String src) { this.src = src; }

    public List<Token> tokens() {
        var out = new ArrayList<Token>();
        while (true) {
            skipSpaceAndComments();
            if (i == src.length()) {
                out.add(new Token(T.EOF, "", line, col));
                return out;
            }
            out.add(next());
        }
    }

    private Token next() {
        int startLine = line, startCol = col, start = i;
        char c = advance();
        if (c >= '0' && c <= '9') {
            while (peek(0) >= '0' && peek(0) <= '9') advance();
            var t = token(T.NUMBER, start, startLine, startCol);
            try {
                Integer.parseInt(t.text());
            } catch (NumberFormatException e) {
                throw new CalcError("number too large '" + t.text() + "' at " + t.at());
            }
            return t;
        }
        if (isWordStart(c)) {
            while (isWordStart(peek(0)) || (peek(0) >= '0' && peek(0) <= '9')) advance();
            boolean let = src.substring(start, i).equals("let");
            return token(let ? T.LET : T.IDENT, start, startLine, startCol);
        }
        T type = switch (c) {
            case '+' -> T.PLUS;
            case '-' -> T.MINUS;
            case '*' -> T.STAR;
            case '/' -> T.SLASH;           // "//" never gets here: skipSpaceAndComments ate it
            case '%' -> T.PERCENT;
            case '^' -> T.CARET;
            case '(' -> T.LPAREN;
            case ')' -> T.RPAREN;
            case '=' -> T.EQUAL;
            case ';' -> T.SEMI;
            default -> throw new CalcError("unexpected character '" + c + "' at " + startLine + ":" + startCol);
        };
        return token(type, start, startLine, startCol);
    }

    private Token token(T type, int start, int startLine, int startCol) {
        return new Token(type, src.substring(start, i), startLine, startCol);
    }

    private void skipSpaceAndComments() {
        while (i < src.length()) {
            char c = peek(0);
            if (c == ' ' || c == '\t' || c == '\r' || c == '\n') advance();
            else if (c == '/' && peek(1) == '/') { while (i < src.length() && peek(0) != '\n') advance(); }
            else return;
        }
    }

    private static boolean isWordStart(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || c == '_';
    }

    private char peek(int ahead) { return i + ahead < src.length() ? src.charAt(i + ahead) : '\0'; }

    private char advance() {
        char c = src.charAt(i++);
        if (c == '\n') { line++; col = 1; } else col++;
        return c;
    }
}
