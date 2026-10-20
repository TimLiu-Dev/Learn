import java.nio.file.Files
import java.nio.file.Path

fun main() {
    val text = Files.readString(Path.of("config.txt"))
    println(text)
}
