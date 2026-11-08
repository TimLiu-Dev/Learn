import java.text.BreakIterator
import java.text.Normalizer

fun sameText(a: String, b: String): Boolean =
    Normalizer.normalize(a, Normalizer.Form.NFC) == Normalizer.normalize(b, Normalizer.Form.NFC)

fun preview(s: String, max: Int): String {
    val boundaries = BreakIterator.getCharacterInstance()
    boundaries.setText(s)
    // The boundary after character number max, or DONE if the text runs out first
    val end = boundaries.next(max)
    return if (end == BreakIterator.DONE || end == s.length) s else s.substring(0, end) + "…"
}
