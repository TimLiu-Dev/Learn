val OLD_USER = ThreadLocal<String>()                 // the old way
val USER: ScopedValue<String> = ScopedValue.newInstance()            // JDK 25+

fun audit(how: String, user: String) = println("$how: acting for $user")

fun main() {
    OLD_USER.set("ada")                              // set, and remember to remove
    try { audit("ThreadLocal", OLD_USER.get()) } finally { OLD_USER.remove() }

    ScopedValue.where(USER, "bob").run { audit("ScopedValue", USER.get()) }
    println("after run: bound = ${USER.isBound}")    // the binding ended with the block
}
