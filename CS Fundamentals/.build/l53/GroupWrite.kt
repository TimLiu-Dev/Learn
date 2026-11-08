import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.attribute.PosixFilePermission.GROUP_WRITE

// Is a file writable by its group? Two ways: the enum set, and one bit of the mode
fun main(args: Array<String>) {
    for (name in args) {
        val perms = Files.getPosixFilePermissions(Path.of(name))
        val mode = toMode(perms)                               // from Perms.kt, compiled together
        println("%-10s %s  set says %-5b  mask says %b".format(
            name, Integer.toOctalString(mode), GROUP_WRITE in perms, mode and 0b000_010_000 != 0))
    }
}
