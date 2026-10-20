inline fun <reified T> Map<String, Any>.getAs(key: String): T? {
    val value = this[key]
    return if (value is T) value else null       // checks the real T, thanks to reified
}

fun main() {
    val settings: Map<String, Any> = mapOf("port" to 8080, "host" to "localhost", "debug" to true)
    val port: Int? = settings.getAs("port")      // T inferred from the expected type
    println(port)
    println(settings.getAs<String>("port"))      // wrong type -> null, not a crash
    println(settings.getAs<String>("host"))
    println(settings.getAs<Boolean>("debug"))
    println(settings.getAs<Int>("missing"))
}
