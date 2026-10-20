// Two ways to handle collisions, same keys, 8 slots, index = key.mod(8)
fun main() {
    val keys = listOf(10, 18, 3, 26, 11)

    // Separate chaining: each slot holds a list of everything that landed there
    val chains = Array(8) { mutableListOf<Int>() }
    for (k in keys) chains[k.mod(8)].add(k)
    println("chaining:")
    chains.forEachIndexed { i, chain -> if (chain.isNotEmpty()) println("  [$i] $chain") }

    // Linear probing: one key per slot; if taken, try the next slot (wrapping)
    val slots = arrayOfNulls<Int>(8)
    println("linear probing:")
    for (k in keys) {
        var i = k.mod(8)
        var probes = 1
        while (slots[i] != null) { i = (i + 1) % 8; probes++ }
        slots[i] = k
        println("  put $k: home ${k.mod(8)}, landed in $i after $probes probe(s)")
    }
    println("  " + slots.joinToString(" ") { it?.toString() ?: "_" })
}
