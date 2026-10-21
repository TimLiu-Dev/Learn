import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

// A JSON subset parser: one function per grammar rule, each calling the others (L18 recursion).
class MiniJson(private val s: String) {
    private var i = 0

    fun parse(): Any? = value().also { skipWs(); require(i == s.length) { "trailing data at $i" } }

    private fun value(): Any? {
        skipWs()
        return when (val c = s[i]) {
            '{' -> obj()
            '[' -> arr()
            '"' -> str()
            't' -> word("true", true)
            'f' -> word("false", false)
            'n' -> word("null", null)
            else -> if (c == '-' || c.isDigit()) num() else error("unexpected '$c' at $i")
        }
    }

    private fun obj(): Map<String, Any?> {
        val m = linkedMapOf<String, Any?>()
        expect('{'); skipWs()
        if (s[i] == '}') { i++; return m }
        while (true) {
            skipWs(); val k = str(); skipWs(); expect(':')
            m[k] = value(); skipWs()
            if (s[i] == ',') i++ else { expect('}'); return m }
        }
    }

    private fun arr(): List<Any?> {
        val l = mutableListOf<Any?>()
        expect('['); skipWs()
        if (s[i] == ']') { i++; return l }
        while (true) {
            l += value(); skipWs()
            if (s[i] == ',') i++ else { expect(']'); return l }
        }
    }

    private fun str(): String {
        expect('"')
        val sb = StringBuilder()
        while (s[i] != '"') {
            if (s[i] == '\\') {
                i++
                sb.append(when (s[i]) { 'n' -> '\n'; 't' -> '\t'; else -> s[i] })   // \" \\ \/ pass through
            } else sb.append(s[i])
            i++
        }
        i++
        return sb.toString()
    }

    private fun num(): Any {
        val start = i
        while (i < s.length && (s[i].isDigit() || s[i] in "-+.eE")) i++
        val text = s.substring(start, i)
        return if (text.any { it in ".eE" }) text.toDouble() else text.toLong()
    }

    private fun word(w: String, v: Any?): Any? { require(s.startsWith(w, i)) { "bad literal at $i" }; i += w.length; return v }
    private fun expect(c: Char) { require(s[i] == c) { "expected '$c' at $i" }; i++ }
    private fun skipWs() { while (i < s.length && s[i].isWhitespace()) i++ }
}

// Turn the library's tree into the same plain values, so the two results can be compared.
fun plain(e: JsonElement): Any? = when (e) {
    is JsonNull -> null
    is JsonObject -> e.mapValues { plain(it.value) }
    is JsonArray -> e.map { plain(it) }
    is JsonPrimitive -> if (e.isString) e.content else
        e.content.toBooleanStrictOrNull() ?: e.content.toLongOrNull() ?: e.content.toDouble()
}

fun main() {
    val inputs = listOf(
        """{"id": 7, "title": "Dune", "tags": ["sci-fi", "classic"]}""",
        """[1, -2.5, 3e2, true, false, null]""",
        """{"quote": "she said \"hi\"\nthen left", "path": "C:\\tmp"}""",
        """{"a": {"b": {"c": [[], {}]}}}""",
        """  "just a string"  """,
    )
    for (text in inputs) {
        val mine = MiniJson(text).parse()
        val lib = plain(Json.parseToJsonElement(text))
        println("${if (mine == lib) "same" else "DIFF"}: $mine")
    }
}
