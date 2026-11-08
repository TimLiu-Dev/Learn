import java.text.BreakIterator

// Cut s to at most maxUnits UTF-16 units (say, a database column's limit)
// without splitting a user-perceived character.
fun truncate(s: String, maxUnits: Int): String {
    if (s.length <= maxUnits) return s
    val boundaries = BreakIterator.getCharacterInstance()
    boundaries.setText(s)
    val end = if (boundaries.isBoundary(maxUnits)) maxUnits else boundaries.preceding(maxUnits)
    return s.substring(0, end)
}

fun main() {
    val bio = "Hi 😀 from café"   // the é is e + combining accent
    println(bio.length)
    println(bio.substring(0, 4))      // cuts the emoji's surrogate pair in half
    println(truncate(bio, 4))
    println(bio.substring(0, 15))     // keeps the e, loses its accent
    println(truncate(bio, 15))
}
