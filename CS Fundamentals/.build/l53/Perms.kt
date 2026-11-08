import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.attribute.PosixFilePermission
import java.nio.file.attribute.PosixFilePermission.*
import java.nio.file.attribute.PosixFilePermissions

// Nine permission bits: rwx for owner, group and others. Kotlin has no octal literals,
// so binary literals with underscores show the three groups (L48).
val BIT = mapOf(
    OWNER_READ to 0b100_000_000, OWNER_WRITE to 0b010_000_000, OWNER_EXECUTE to 0b001_000_000,
    GROUP_READ to 0b000_100_000, GROUP_WRITE to 0b000_010_000, GROUP_EXECUTE to 0b000_001_000,
    OTHERS_READ to 0b000_000_100, OTHERS_WRITE to 0b000_000_010, OTHERS_EXECUTE to 0b000_000_001,
)

fun toMode(perms: Set<PosixFilePermission>): Int = perms.sumOf { BIT.getValue(it) }
fun fromMode(mode: Int): Set<PosixFilePermission> = BIT.filterValues { mode and it != 0 }.keys

fun show(f: Path): String {
    val perms = Files.getPosixFilePermissions(f)
    return "${PosixFilePermissions.toString(perms)}  ${Integer.toOctalString(toMode(perms))}"
}

fun main() {
    val f = Path.of("secret.txt")
    Files.deleteIfExists(f)
    Files.createFile(f)                                   // the umask decides these bits
    println("created:        ${show(f)}")
    Files.setPosixFilePermissions(f, PosixFilePermissions.fromString("rw-r-----"))
    println("rw-r-----:      ${show(f)}")
    Files.setPosixFilePermissions(f, fromMode("750".toInt(8)))
    println("mode 750:       ${show(f)}")
    val mode = toMode(Files.getPosixFilePermissions(f))
    println("owner can run:  ${mode and 0b001_000_000 != 0}")
    println("others can read: ${mode and 0b000_000_100 != 0}")
}
