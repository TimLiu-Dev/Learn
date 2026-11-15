data class Config(val hosts: List<String>)

fun main() {
    val hosts = mutableListOf("a")
    val shared = Config(hosts)
    val copied = Config(hosts.toList())
    hosts += "b"
    println("${shared.hosts.size} ${copied.hosts.size} ${shared == copied}")
}
