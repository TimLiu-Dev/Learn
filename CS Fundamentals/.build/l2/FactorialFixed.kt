fun main() {
    // Fix 1: a bigger type
    var big = 1L
    for (n in 1..20) big *= n
    println("20! = $big")

    // Fix 2: fail loudly instead of silently wrapping
    var f = 1
    for (n in 1..15) {
        f = Math.multiplyExact(f, n)
        println("$n! = $f")
    }
}
