sealed interface Command
data class Add(val item: String) : Command
data object Clear : Command

fun main() {
    val log: List<Command> = listOf(Add("tea"), Clear, Add("tea"), Clear)
    println(log.toSet())
}
