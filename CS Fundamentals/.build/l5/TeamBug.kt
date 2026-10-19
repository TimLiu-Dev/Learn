class Team(val players: MutableList<String>)      // stores the caller's list as-is

fun main() {
    val names = mutableListOf("Ada", "Linus")
    val team = Team(names)
    names.add("Mallory")                // the caller keeps using their list...
    println(team.players)               // ...and the team changed behind its back
    team.players.clear()                // anyone can also empty the team from outside
    println(names)
}
