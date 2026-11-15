import java.util.TreeMap

fun main() {
    val raw = "HTTP/1.1 200 OK\r\ncontent-length: 5\r\nContent-Type: text/plain\r\n\r\nhello"
    val plain = HashMap<String, String>()
    val ci = TreeMap<String, String>(String.CASE_INSENSITIVE_ORDER)
    for (line in raw.substringBefore("\r\n\r\n").split("\r\n").drop(1)) {
        val (name, value) = line.split(": ", limit = 2)
        plain[name] = value
        ci[name] = value
    }
    println("${plain["Content-Length"]} ${ci["Content-Length"]}")
}
