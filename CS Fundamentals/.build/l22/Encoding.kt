import java.nio.charset.Charset
import java.nio.file.Files

fun main() {
    val text = "café 😀"
    val file = Files.createTempFile("l22", ".txt")
    Files.writeString(file, text)                     // no charset given: UTF-8 (JEP 400)
    val bytes = Files.readAllBytes(file)

    println("length: ${text.length} chars, ${text.codePointCount(0, text.length)} code points, ${bytes.size} bytes")
    println(bytes.joinToString(" ") { "%02x".format(it) })
    println("decoded as UTF-8:        " + String(bytes, Charsets.UTF_8))
    println("decoded as windows-1252: " + String(bytes, Charset.forName("windows-1252")))
    println("default charset: ${Charset.defaultCharset()}")
    Files.delete(file)
}
