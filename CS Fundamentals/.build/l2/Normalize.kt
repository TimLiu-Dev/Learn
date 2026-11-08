import java.text.BreakIterator
import java.text.Normalizer

// Count user-perceived characters: grapheme clusters (UAX #29)
fun graphemes(s: String): Int {
    val boundaries = BreakIterator.getCharacterInstance()
    boundaries.setText(s)
    var count = 0
    while (boundaries.next() != BreakIterator.DONE) count++
    return count
}

fun main() {
    val composed = "café"       // é as ONE code point, U+00E9
    val decomposed = "café"    // e + U+0301 COMBINING ACUTE ACCENT
    println("$composed $decomposed") // they look the same...
    println(composed == decomposed)  // ...but the Chars differ
    println("${composed.length} ${decomposed.length}")

    val nfc = Normalizer.normalize(decomposed, Normalizer.Form.NFC)
    println(composed == nfc)         // equal once both are in NFC
    val nfd = Normalizer.normalize(composed, Normalizer.Form.NFD)
    println(nfd == decomposed)

    val family = "👨‍👩‍👧"   // man, ZWJ, woman, ZWJ, girl
    for (s in listOf(decomposed, family)) {
        println("$s length=${s.length} codePoints=${s.codePointCount(0, s.length)} graphemes=${graphemes(s)}")
    }
}
