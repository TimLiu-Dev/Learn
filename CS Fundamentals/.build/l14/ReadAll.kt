import java.io.IOException
import java.nio.file.Files

fun main() {
    val dir = Files.createTempDirectory("l14")
    val a = Files.writeString(dir.resolve("a.txt"), "alpha")
    val b = Files.writeString(dir.resolve("b.txt"), "beta")
    println(listOf(a, b).map(Files::readString))      // compiles: Kotlin has no checked exceptions
    try {
        listOf(a, dir.resolve("missing.txt")).map(Files::readString)
    } catch (e: IOException) {                         // nothing forced this catch: remembering it is on you
        println("caught ${e::class.simpleName}")
    }
}
