package demo

data class Config(val excitement: String)

/** The "application code": plain methods, wired up only by annotations. */
class Greetings(private val config: Config) {

    @Command(name = "greet", help = "greet NAME   say hello")
    fun greet(args: List<String>): String = "Hello, ${args.firstOrNull() ?: "stranger"}${config.excitement}"

    @Command(name = "add", help = "add N...     sum whole numbers")
    fun add(args: List<String>): Int = args.sumOf { it.toInt() }

    @Command(name = "shout", help = "shout WORDS  uppercase them")
    fun shout(args: List<String>): String = args.joinToString(" ").uppercase() + config.excitement

    fun internalHelper(): String = "not annotated, so never routed"
}
