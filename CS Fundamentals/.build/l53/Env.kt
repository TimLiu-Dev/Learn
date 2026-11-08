// Environment variables: a map of strings every process inherits from its parent
fun main() {
    println("GREETING in me:  ${System.getenv("GREETING") ?: "(not set)"}")
    println("HOME in me:      ${System.getenv("HOME")}")
    try {
        System.getenv()["GREETING"] = "changed"     // compiles: Java's Map is a platform type (L9)
    } catch (e: UnsupportedOperationException) {
        println("can't change my own environment: ${e.javaClass.simpleName}")
    }
    val child = ProcessBuilder("sh", "-c", "echo \"GREETING in child: \$GREETING\"")
    child.environment()["GREETING"] = "set by the parent"  // a copy, only for this child
    child.inheritIO().start().waitFor()
    println("GREETING in me:  ${System.getenv("GREETING") ?: "(not set)"}")
}
