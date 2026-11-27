package budget

import kotlin.test.Test
import kotlin.test.assertEquals

class RulesTest {
    private fun rules(vararg lines: String) = (parseRules(lines.toList()) as Parsed.Ok).value

    @Test
    fun `first matching rule wins`() {
        val r = rules("tesco express -> Snacks", "tesco -> Groceries")
        assertEquals("Snacks", r.categorise("TESCO EXPRESS 123"))
        assertEquals("Groceries", r.categorise("Tesco Stores"))
    }

    @Test
    fun `no match is Uncategorised`() {
        assertEquals(UNCATEGORISED, rules("tesco -> Groceries").categorise("Boots"))
    }

    @Test
    fun `substring and regex matching ignore case, including accents`() {
        val r = rules("# comment", "", "café -> Coffee", "/^tfl\\b/ -> Transport")
        assertEquals("Coffee", r.categorise("CAFÉ NERO"))
        assertEquals("Transport", r.categorise("TfL Travel Charge"))
        assertEquals(UNCATEGORISED, r.categorise("Netflix TFL"))
    }

    @Test
    fun `bad lines name the line and the reason`() {
        assertEquals(Parsed.Bad("line 2: missing '->'"), parseRules(listOf("a -> B", "tesco Groceries")))
        assertEquals(Parsed.Bad("line 1: empty category"), parseRules(listOf("tesco ->  ")))
        assertEquals(Parsed.Bad("line 1: empty pattern"), parseRules(listOf("// -> X")))
        assertEquals(Parsed.Bad("line 3: invalid regex '/[a/'"), parseRules(listOf("#", "a -> B", "/[a/ -> X")))
    }
}
