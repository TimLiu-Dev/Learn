fun main() {
    val x = 100
    val m = (x - 128) shr 31
    println("$m ${m.inv() and x} ${(x - 128) ushr 31}")
}
