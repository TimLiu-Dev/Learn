fun intSetting(settings: Map<String, String?>, key: String, default: Int): Int =
    settings[key]?.toIntOrNull() ?: default

fun main() {
    val settings = mapOf("port" to "8080", "timeout" to "abc", "host" to null)
    println("port = ${intSetting(settings, "port", 80)}")
    println("timeout = ${intSetting(settings, "timeout", 30)}")
    println("retries = ${intSetting(settings, "retries", 3)}")
    println("host = ${settings["host"] ?: "localhost"}")
}
