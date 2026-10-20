fun main() {
    val buckets = arrayOfNulls<String>(16)
    val h = "banana".hashCode()

    println("h          = $h")
    println("h % 16     = ${h % 16}")              // remainder keeps the sign of h
    println("h.mod(16)  = ${h.mod(16)}")           // floor modulus: 0..15, always
    println("h and 15   = ${h and 15}")            // same, but only for powers of two
    println("spread     = ${(h xor (h ushr 16)) and 15}")  // what HashMap does first

    buckets[h.mod(16)] = "banana"                  // fine
    buckets[h % 16] = "banana"                     // index -11
}
