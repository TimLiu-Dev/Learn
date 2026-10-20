// An immutable value can be shared by any number of threads with no locks at all.
data class Config(val name: String, val hosts: List<String>)    // read-only view, not immutable

fun safeConfig(name: String, hosts: List<String>) = Config(name, hosts.toList()) // private copy

fun main() {
    val hosts = mutableListOf("a.example", "b.example")
    val config = Config("prod", hosts)
    val safe = safeConfig("prod", hosts)

    hosts += "evil.example"                                     // someone keeps the original list

    println(config)
    println(safe)
}
