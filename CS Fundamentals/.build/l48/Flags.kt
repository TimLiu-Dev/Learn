import java.util.EnumSet

const val READ = 4    // 100
const val WRITE = 2   // 010
const val EXEC = 1    // 001

fun rwx(f: Int) = (if (f and READ != 0) "r" else "-") + (if (f and WRITE != 0) "w" else "-") + (if (f and EXEC != 0) "x" else "-")

enum class Perm { READ, WRITE, EXEC }

fun main() {
    var f = READ or EXEC                 // set two flags
    println("start      ${rwx(f)}")
    f = f or WRITE                       // set
    println("add write  ${rwx(f)}")
    f = f and EXEC.inv()                 // clear
    println("drop exec  ${rwx(f)}")
    f = f xor READ                       // toggle
    println("flip read  ${rwx(f)}")
    println("can write? ${f and WRITE != 0}")

    // Unix permissions are three of these, written in octal: 7 = rwx, 5 = r-x
    val mode = "755".toInt(8)            // Kotlin has no octal literals
    println("755 = $mode = ${Integer.toBinaryString(mode)} -> ${rwx(mode shr 6)}${rwx((mode shr 3) and 7)}${rwx(mode and 7)}")

    // The typesafe version: an EnumSet is a bit vector underneath
    val perms = EnumSet.of(Perm.READ, Perm.EXEC)
    perms += Perm.WRITE
    perms -= Perm.EXEC
    println("EnumSet: $perms   contains WRITE? ${Perm.WRITE in perms}")
}
