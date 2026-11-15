fun main() {
    val line = "GET  /books 200"                         // two spaces after GET
    val cutStyle = line.split(" ")                      // like cut -d' '
    val awkStyle = line.trim().split(Regex("\\s+"))     // like awk's default fields
    println("[" + cutStyle[1] + "] [" + awkStyle[1] + "]")
}
