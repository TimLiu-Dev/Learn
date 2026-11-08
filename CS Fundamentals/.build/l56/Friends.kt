import java.sql.DriverManager

val edges = listOf("ana" to "ben", "ben" to "cai", "cai" to "dee", "ana" to "eve", "eve" to "cai", "dee" to "fay")

// L21's breadth-first search, stopping after maxHops.
fun bfs(start: String, maxHops: Int): Map<String, Int> {
    val next = edges.groupBy({ it.first }, { it.second })
    val hops = linkedMapOf(start to 0)
    val queue = ArrayDeque(listOf(start))
    while (queue.isNotEmpty()) {
        val p = queue.removeFirst()
        if (hops.getValue(p) == maxHops) continue
        for (q in next[p].orEmpty()) if (q !in hops) { hops[q] = hops.getValue(p) + 1; queue.addLast(q) }
    }
    return hops
}

fun main() {
    DriverManager.getConnection("jdbc:sqlite::memory:").use { c ->
        c.createStatement().use { it.execute("CREATE TABLE friends (a TEXT, b TEXT)") }
        c.prepareStatement("INSERT INTO friends VALUES (?, ?)").use { ins ->
            for ((a, b) in edges) { ins.setString(1, a); ins.setString(2, b); ins.executeUpdate() }
        }
        val sql = """
            WITH RECURSIVE reach(person, hops) AS (
              SELECT ?, 0
              UNION
              SELECT f.b, r.hops + 1 FROM reach r JOIN friends f ON f.a = r.person WHERE r.hops < ?
            )
            SELECT person, min(hops) FROM reach GROUP BY person""".trimIndent()
        for (maxHops in 1..4) {
            val fromSql = c.prepareStatement(sql).use { q ->
                q.setString(1, "ana"); q.setInt(2, maxHops)
                q.executeQuery().use { rs -> buildMap { while (rs.next()) put(rs.getString(1), rs.getInt(2)) } }
            }
            val fromBfs = bfs("ana", maxHops)
            println("within $maxHops: ${fromBfs.toSortedMap()}  same as SQL: ${fromSql == fromBfs}")
        }
    }
}
