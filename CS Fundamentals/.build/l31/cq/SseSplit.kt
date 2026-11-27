fun field(line: String): String {
    val (name, value) = line.split(":")      // destructuring takes the first two parts
    return "$name=[${value.removePrefix(" ")}]"
}

fun main() {
    println(field("data: 12:30") + " " + field("data:  ok"))
}
