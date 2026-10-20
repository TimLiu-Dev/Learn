data class Member(var name: String)               // var in a key: asking for trouble

fun main() {
    val points = HashMap<Member, Int>()
    val ada = Member("ada")
    points[ada] = 100
    println("before: ${points[ada]}")

    ada.name = "ada lovelace"                     // hashCode changes, the entry doesn't move
    println("same object: ${points[ada]}")
    println("old name: ${points[Member("ada")]}")
    println("size ${points.size}, contents $points")
}
