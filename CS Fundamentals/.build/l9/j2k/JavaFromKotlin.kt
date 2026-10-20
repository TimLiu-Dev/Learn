import java.io.IOException

fun main() {
    val p = Person("Ada")
    println(p.name)                    // calls getName()
    p.name = "Ada Lovelace"            // calls setName(...)
    p.isActive = true                  // calls setActive(true)
    println("${p.name} active=${p.isActive}")
    println(p.`is`("Ada Lovelace"))    // backticks: a Java method named like a Kotlin keyword
    println(Person.anonymous().name)   // Java static method: ClassName.method()

    val nick: String? = p.nickname     // platform type -> we choose String?: safe
    println(nick ?: "(no nickname)")

    Tasks.runTwice { println("tick") }                  // lambda becomes a Runnable
    println(Tasks.apply("kotlin") { it.uppercase() })   // lambda becomes a Tasks.Formatter

    try {
        Tasks.readConfig("app.conf")   // Kotlin doesn't force you to catch IOException
    } catch (e: IOException) {
        println("caught: ${e.message}")
    }

    val email: String? = Directory.findEmail("grace")   // @Nullable -> must be String?
    println(email ?: "(no email)")

    val nickname: String = p.nickname  // compiles (platform type)... and fails right here
    println(nickname.length)
}
