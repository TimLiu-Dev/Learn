data class Team(val name: String, val members: MutableList<String>)

fun main() {
    val original = Team("Core", mutableListOf("Ada"))
    val renamed = original.copy(name = "Platform")
    renamed.members.add("Linus")
    println(original)
    println(renamed)
    println(original == renamed)
}
