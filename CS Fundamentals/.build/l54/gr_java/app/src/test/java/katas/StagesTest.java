package katas;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Test;

// One small test per pipeline stage, written before the stage itself.
class StagesTest {
    private static List<String> texts(String src) {
        return new Lexer(src).tokens().stream().map(Token::text).toList();
    }

    @Test void scannerGroupsCharactersIntoTokensAndSkipsComments() {
        assertEquals(List.of("let", "x1", "=", "42", ";", "x1", "/", "2", ""), texts("let x1 = 42; // hi\nx1 / 2"));
    }

    @Test void scannerUsesMaximalMunchForNamesAndKeywords() {
        assertEquals(List.of(T.IDENT, T.LET, T.EOF), new Lexer("letter let").tokens().stream().map(Token::type).toList());
    }

    @Test void scannerErrorsCarryLineAndColumn() {
        var e = assertThrows(CalcError.class, () -> new Lexer("1 +\n  @").tokens());
        assertEquals("unexpected character '@' at 2:3", e.getMessage());
    }

    @Test void parserGivesPrecedenceAndAssociativity() {
        assertEquals("(- (- 8 3) (* 2 2))", Parser.show(Parser.parse("8 - 3 - 2 * 2").result()));
        assertEquals("(^ 2 (^ 3 2))", Parser.show(Parser.parse("2 ^ 3 ^ 2").result()));
        assertEquals("(neg (^ 2 2))", Parser.show(Parser.parse("-2 ^ 2").result()));
    }

    @Test void parserReportsWhatItExpected() {
        var e = assertThrows(CalcError.class, () -> Parser.parse("let x = (1 + 2; x"));
        assertEquals("expected ')' at 1:15 but found ';'", e.getMessage());
    }

    @Test void interpreterEvaluatesWithAnEnvironment() {
        assertEquals(new Outcome.Value(24), Calc.runInterpreted("let x = 5; let x = x * x; x - 1"));
        assertEquals(new Outcome.Value(Integer.MIN_VALUE), Calc.runInterpreted("2147483647 + 1"));
        assertEquals(new Outcome.Error("division by zero at 1:4"), Calc.runInterpreted("10 / (5 - 5)"));
    }

    @Test void compilerEmitsTheSameShapeJavacDoes() {
        Chunk chunk = new Compiler().compile(Parser.parse("let x = 3; x * (x + 1)"));
        var ops = Vm.disassemble(chunk).lines().skip(1)
                .map(l -> l.substring(l.indexOf(": ") + 2).split(" ")[0]).toList();
        assertEquals(List.of("push", "store", "load", "load", "push", "add", "mul", "return"), ops);
        assertEquals(3, chunk.maxStack());
    }

    @Test void vmAgreesWithTheInterpreterOnALongProgram() {
        String src = IntStream.rangeClosed(1, 200)
                .mapToObj(i -> "let v" + i + " = v" + (i - 1) + " * 3 + " + i + " % 7;")
                .collect(Collectors.joining()).replace("v0", "1") + "v200";
        assertEquals(Calc.runInterpreted(src), Calc.runCompiled(src));
    }
}
