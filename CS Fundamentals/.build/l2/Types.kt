fun main() {
    val count = 42              // Int
    val big = 3_000_000_000L    // Long
    val price = 9.99            // Double
    val ratio = 0.5f            // Float
    val initial = 'K'           // Char
    val done = false            // Boolean
    val mask = 0xFF             // Int, written in hex
    val flags = 0b1010          // Int, written in binary
    println("$count $big $price $ratio $initial $done $mask $flags")

    var score = 10              // var: can be reassigned
    score += 5
    println(score)
}
