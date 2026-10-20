import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.*

fun main() {
    val dir = Files.createTempDirectory("l22")
    val notes: Path = dir / "notes.txt"                         // kotlin.io.path: '/' joins paths

    notes.writeText("milk\neggs\n")                             // whole file at once
    notes.appendText("bread\n")
    println("readText: " + notes.readText().replace("\n", "|"))

    val longest = notes.useLines { lines -> lines.maxBy { it.length } }  // lazy, closed for you
    println("useLines: longest is $longest")

    notes.bufferedReader().use { r -> println("first line: ${r.readLine()}") }
    notes.toFile().forEachLine { println("  java.io.File line: $it") }

    (dir / "sub").createDirectory()
    (dir / "sub" / "todo.txt").writeText("call mum\n")
    Files.walk(dir).use { paths ->
        paths.filter { it.isRegularFile() }
            .forEach { println("walk: ${dir.relativize(it)} (${it.fileSize()} bytes)") }
    }

    val rel = Path("data/../report.csv")
    println("relative: $rel  absolute? ${rel.isAbsolute}  normalized: ${rel.normalize()}")
    val cwd = Path("").absolute()                               // the working directory
    println("working dir: .../${cwd.name}  absolute(): .../${cwd.name}/${rel.normalize().absolute().name}")
    dir.toFile().deleteRecursively()
}
