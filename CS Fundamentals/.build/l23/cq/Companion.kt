val log = mutableListOf<String>()

class Box {
    companion object {
        init { log += "companion" }
    }

    init { log += "box" }
}

fun main() {
    log += "main"
    Box()
    Box()
    println(log)
}
