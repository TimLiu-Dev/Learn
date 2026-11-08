class Box(val size: Int) {
    init {
        print("init$size ")
    }

    constructor() : this(1) {
        print("secondary ")
    }
}

fun main() {
    Box()
    Box(2)
    println("done")
}
