package katas

fun main() {
    for ((a, b) in listOf(6 to 7, 13 to 20, 255 to 255)) {
        val cpu = multiply(a, b)
        println("$a * $b = ${cpu.mem[0x82]} (mod 256) in ${cpu.steps} instructions")
    }
}
