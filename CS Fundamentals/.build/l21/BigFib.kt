import java.math.BigInteger

fun main() {
    var prev = 0L
    var cur = 1L
    for (n in 1..93) {
        val next = prev + cur
        prev = cur
        cur = next
        if (n >= 91) println("fib($n) as Long   = $prev")
    }

    var a = BigInteger.ZERO
    var b = BigInteger.ONE
    repeat(93) {
        val next = a + b          // operator overloading: BigInteger.plus
        a = b
        b = next
    }
    println("fib(93) as BigInteger = $a")
}
