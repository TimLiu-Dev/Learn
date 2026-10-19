class Counter(var count: Int)

fun main() {
    val p = Counter(1)
    val q = Counter(1)
    val r = p
    r.count += 10
    val all = listOf(p, q, r)        // Java: List.of(p, q, r)
    all[1].count = 7
    println("${p.count} ${q.count} ${r.count}")
    println(all.size)
    println("${all[0].count} ${all[2].count}")
}
