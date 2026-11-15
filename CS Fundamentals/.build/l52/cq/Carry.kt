fun main() {
    var x = 0b0110
    var y = 0b0011
    while (y != 0) {
        val carry = (x and y) shl 1   // every column where both bits are 1 carries left
        x = x xor y                   // add each column, ignoring carries
        y = carry
    }
    println(x)
}
