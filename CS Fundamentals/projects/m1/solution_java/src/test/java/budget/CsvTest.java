package budget;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.junit.jupiter.api.Test;

class CsvTest {
    @Test
    void splitsPlainFields() {
        assertEquals(Parsed.ok(List.of("2026-01-03", "Tesco", "-23.40")), Csv.splitLine("2026-01-03,Tesco,-23.40"));
    }

    @Test
    void keepsCommasInsideQuotesAndUnescapesDoubledQuotes() {
        assertEquals(Parsed.ok(List.of("2026-01-03", "Joe's \"Diner\", Leeds", "-9.99")),
            Csv.splitLine("2026-01-03,\"Joe's \"\"Diner\"\", Leeds\",-9.99"));
    }

    @Test
    void aTrailingCommaMakesAnEmptyLastField() {
        assertEquals(Parsed.ok(List.of("a", "b", "")), Csv.splitLine("a,b,"));
    }

    @Test
    void reportsBrokenQuoting() {
        assertEquals(Parsed.bad("unterminated quoted field"), Csv.splitLine("2026-01-03,\"Tesco,-1.00"));
        assertEquals(Parsed.bad("text after closing quote"), Csv.splitLine("2026-01-03,\"Tesco\"x,-1.00"));
    }
}
