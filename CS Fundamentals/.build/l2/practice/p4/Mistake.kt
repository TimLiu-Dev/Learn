import java.text.Normalizer

fun sameText(a: String, b: String): Boolean =
    Normalizer.normalize(a, Normalizer.Form.NFC) == Normalizer.normalize(b, Normalizer.Form.NFC)

// Counts code points: keeps surrogate pairs together, but not accents with their letters
fun preview(s: String, max: Int): String {
    if (s.codePointCount(0, s.length) <= max) return s
    return s.substring(0, s.offsetByCodePoints(0, max)) + "…"
}
