class Team(players: List<String>) {
    val players: List<String> = players.toList()   // copy on the way in, read-only on the way out
}

fun main() {
    val names = mutableListOf("Ada", "Linus")
    val team = Team(names)
    names.add("Mallory")
    println(team.players)
    // team.players.add("Eve")          // won't compile: List has no add
}
