package budget

import kotlin.test.Test
import kotlin.test.assertEquals

class CsvTest {
    @Test
    fun `splits plain fields`() {
        assertEquals(Parsed.Ok(listOf("2026-01-03", "Tesco", "-23.40")), splitCsvLine("2026-01-03,Tesco,-23.40"))
    }

    @Test
    fun `keeps commas inside quotes and unescapes doubled quotes`() {
        assertEquals(
            Parsed.Ok(listOf("2026-01-03", "Joe's \"Diner\", Leeds", "-9.99")),
            splitCsvLine("2026-01-03,\"Joe's \"\"Diner\"\", Leeds\",-9.99"),
        )
    }

    @Test
    fun `a trailing comma makes an empty last field`() {
        assertEquals(Parsed.Ok(listOf("a", "b", "")), splitCsvLine("a,b,"))
    }

    @Test
    fun `reports broken quoting`() {
        assertEquals(Parsed.Bad("unterminated quoted field"), splitCsvLine("2026-01-03,\"Tesco,-1.00"))
        assertEquals(Parsed.Bad("text after closing quote"), splitCsvLine("2026-01-03,\"Tesco\"x,-1.00"))
    }
}
