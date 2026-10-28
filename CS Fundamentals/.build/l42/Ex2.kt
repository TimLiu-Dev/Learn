// Uses Ring and modShard from Sharding.kt: kotlinc Sharding.kt Ex2.kt ...
fun main() {
    val keys = (0 until 100_000).map { "user:$it" }
    val ring = Ring(100)
    listOf("n1", "n2", "n3", "n4").forEach(ring::add)
    val before = keys.associateWith(ring::nodeFor)
    ring.remove("n3")                                            // n3 crashed for good
    val after = keys.associateWith(ring::nodeFor)
    val changed = keys.filter { before[it] != after[it] }
    println("ring: n3 removed, %,d keys move; all were on n3: %s".format(changed.size, changed.all { before[it] == "n3" }))
    println("      where n3's keys went: " + changed.groupingBy { after.getValue(it) }.eachCount().toSortedMap())
    val modMoved = keys.count { modShard(it, 4) != modShard(it, 3) }
    println("mod N, 4 -> 3 nodes: %,d keys move".format(modMoved))
}
