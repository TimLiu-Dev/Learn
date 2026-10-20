data class Follow(val from: String, val to: String)

fun main() {
    val follows = listOf(
        Follow("ana", "ben"), Follow("ana", "cat"), Follow("ben", "cat"),
        Follow("cat", "ana"), Follow("dan", "cat"), Follow("eve", "dan"),
    )
    // Directed: the edge goes into the "from" list only.
    val following = mutableMapOf<String, MutableList<String>>()
    val followers = mutableMapOf<String, Int>()
    for ((from, to) in follows) {
        following.getOrPut(from) { mutableListOf() }.add(to)
        followers[to] = (followers[to] ?: 0) + 1
    }
    println("following: $following")
    println("followers: $followers")
    println("most followed: ${followers.maxBy { it.value }.key}")
    val mutual = follows.filter { (a, b) -> a < b && a in following[b].orEmpty() }
    println("mutual: ${mutual.map { "${it.from}<->${it.to}" }}")
}
