import java.nio.file.Files
import java.nio.file.LinkOption.NOFOLLOW_LINKS
import java.nio.file.Path
import java.nio.file.StandardCopyOption.ATOMIC_MOVE
import kotlin.io.path.*

fun describe(p: Path) =
    "${p.name}: inode ${Files.getAttribute(p, "unix:ino", NOFOLLOW_LINKS)}, " +
    "links ${Files.getAttribute(p, "unix:nlink", NOFOLLOW_LINKS)}, symlink ${p.isSymbolicLink()}"

fun main() {
    val dir = createTempDirectory("links")
    val original = (dir / "notes.txt").apply { writeText("hello\n") }
    val hard = Files.createLink(dir / "hard.txt", original)          // a second name, same inode
    val soft = Files.createSymbolicLink(dir / "soft.txt", Path("notes.txt"))  // a file holding a path
    listOf(original, hard, soft).forEach { println(describe(it)) }
    println("soft.txt points to: ${soft.readSymbolicLink()}")

    original.deleteExisting()
    println("after deleting notes.txt:")
    println("  hard.txt still reads: ${hard.readText().trim()}")
    println("  soft.txt exists? ${soft.exists()}  (link itself: ${soft.exists(NOFOLLOW_LINKS)})")

    // The deploy trick: build the new link beside the old one, then rename it over the top.
    (dir / "v1").createDirectory(); (dir / "v2").createDirectory()
    val current = Files.createSymbolicLink(dir / "current", Path("v1"))
    val next = Files.createSymbolicLink(dir / "current.tmp", Path("v2"))
    Files.move(next, current, ATOMIC_MOVE)                          // one rename(2): never missing
    println("current -> ${current.readSymbolicLink()}")
    dir.toFile().deleteRecursively()
}
