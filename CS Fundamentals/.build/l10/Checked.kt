import java.nio.file.Files
import java.nio.file.Path

fun loadConfig(name: String): String =
    Files.readString(Path.of(name))     // same JDK method; Kotlin doesn't force you to handle it

fun main() {
    println(loadConfig("app.conf"))      // compiles... and fails at runtime
}
