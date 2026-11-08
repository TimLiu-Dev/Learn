import java.text.Normalizer

fun main() {
    val a = "café"     // é as one code point
    val b = "café"    // e + combining accent
    Normalizer.normalize(b, Normalizer.Form.NFC)
    val c = Normalizer.normalize(b, Normalizer.Form.NFC)
    println("${a == b} ${a == c} ${b.length}")
}
