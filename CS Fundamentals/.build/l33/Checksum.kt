import java.io.File
import java.security.MessageDigest

// Streams the file in 8 KB chunks, so a 4 GB file never sits in memory (L22)
fun sha256File(file: File): String {
    val md = MessageDigest.getInstance("SHA-256")
    file.inputStream().use { input ->
        val buf = ByteArray(8192)
        while (true) {
            val n = input.read(buf)
            if (n < 0) break
            md.update(buf, 0, n)
        }
    }
    return md.digest().joinToString("") { "%02x".format(it) }
}

fun main(args: Array<String>) {
    val file = File(args[0])
    val expected = args[1]
    val actual = sha256File(file)
    println("actual:   $actual")
    println(if (actual == expected) "OK: matches" else "MISMATCH: do not trust this file")
}
