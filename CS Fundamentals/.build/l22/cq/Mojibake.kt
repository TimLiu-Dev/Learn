fun main() {
    val bytes = "café".toByteArray()                  // UTF-8 by default
    val wrong = String(bytes, Charsets.ISO_8859_1)    // one char per byte
    println("${bytes.size} ${wrong.length} $wrong")
}
