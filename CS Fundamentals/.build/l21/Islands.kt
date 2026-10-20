val map = listOf(
    "##...#",
    "#....#",
    "...##.",
    "#..##.",
    "#.....",
)

// Flood-fill one island, marking every land cell it touches as seen.
fun sink(r: Int, c: Int, seen: Array<BooleanArray>) {
    if (r !in map.indices || c !in map[r].indices) return
    if (map[r][c] != '#' || seen[r][c]) return
    seen[r][c] = true
    sink(r - 1, c, seen); sink(r + 1, c, seen); sink(r, c - 1, seen); sink(r, c + 1, seen)
}

fun main() {
    val seen = Array(map.size) { BooleanArray(map[0].length) }
    var islands = 0
    for (r in map.indices) {
        for (c in map[r].indices) {
            if (map[r][c] == '#' && !seen[r][c]) {
                islands++
                sink(r, c, seen)
            }
        }
    }
    println("islands: $islands")
}
