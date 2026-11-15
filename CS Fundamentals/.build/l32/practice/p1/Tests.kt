import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Test

class JsonWriterTest {
    @Test fun `a plain string is just quoted`() {
        assertEquals("\"Dune\"", quote("Dune"))
    }

    @Test fun `quotes and backslashes are escaped`() {
        val expected = "\"" + """she said \"hi\" in C:\\tmp""" + "\""
        assertEquals(expected, quote("""she said "hi" in C:\tmp"""))
    }

    @Test fun `control characters are escaped`() {
        assertEquals("\"a\\nb\\tc\\u0001\"", quote("a\nb\tc\u0001"))
    }

    @Test fun `quote agrees with kotlinx serialization`() {
        val nasty = "tab\there \"q\" back\\slash\r\n\b\u000C\u001F é ✓"
        assertEquals(Json.encodeToString(nasty), quote(nasty))
    }

    @Test fun `nested lists and maps encode compactly`() {
        val book = mapOf("id" to 7, "title" to "Dune", "tags" to listOf("sci-fi", null, true), "extra" to mapOf<String, Any?>())
        assertEquals("""{"id":7,"title":"Dune","tags":["sci-fi",null,true],"extra":{}}""", encode(book))
    }

    @Test fun `map keys are escaped too`() {
        assertEquals("""{"say \"hi\"":1}""", encode(mapOf("say \"hi\"" to 1)))
    }
}
