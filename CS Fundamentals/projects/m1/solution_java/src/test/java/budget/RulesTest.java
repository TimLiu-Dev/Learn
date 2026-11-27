package budget;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.junit.jupiter.api.Test;

class RulesTest {
    private static Rules rules(String... lines) {
        return ((Parsed.Ok<Rules>) Rules.parse(List.of(lines))).value();
    }

    @Test
    void firstMatchingRuleWins() {
        Rules r = rules("tesco express -> Snacks", "tesco -> Groceries");
        assertEquals("Snacks", r.categorise("TESCO EXPRESS 123"));
        assertEquals("Groceries", r.categorise("Tesco Stores"));
    }

    @Test
    void noMatchIsUncategorised() {
        assertEquals(Rules.UNCATEGORISED, rules("tesco -> Groceries").categorise("Boots"));
    }

    @Test
    void substringAndRegexIgnoreCaseIncludingAccents() {
        Rules r = rules("# comment", "", "café -> Coffee", "/^zoë/ -> Bakery", "/^tfl\\b/ -> Transport", "a.b -> Dots");
        assertEquals("Coffee", r.categorise("CAFÉ NERO"));
        assertEquals("Bakery", r.categorise("ZOË'S BAKERY"));
        assertEquals("Transport", r.categorise("TfL Travel Charge"));
        assertEquals(Rules.UNCATEGORISED, r.categorise("Netflix TFL"));
        assertEquals(Rules.UNCATEGORISED, r.categorise("axb"), "a substring rule's dot is a plain dot");
    }

    @Test
    void badLinesNameTheLineAndTheReason() {
        assertEquals(Parsed.bad("line 2: missing '->'"), Rules.parse(List.of("a -> B", "tesco Groceries")));
        assertEquals(Parsed.bad("line 1: empty category"), Rules.parse(List.of("tesco ->  ")));
        assertEquals(Parsed.bad("line 1: empty pattern"), Rules.parse(List.of("// -> X")));
        assertEquals(Parsed.bad("line 3: invalid regex '/[a/'"), Rules.parse(List.of("#", "a -> B", "/[a/ -> X")));
    }
}
