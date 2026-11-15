fun main() {
    val keys = 0 until 12
    val moved = keys.count { it % 3 != it % 4 }      // shard = key mod N, going from 3 to 4 shards
    println("moved $moved of ${keys.count()}")
}
