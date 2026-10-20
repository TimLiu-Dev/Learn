object AppInfo {
    const val VERSION = "1.4.2"
    fun describe(): String = "Shapes v$VERSION"
}

class Square private constructor(val side: Int) {
    val area: Int
        get() = side * side

    companion object {
        var created = 0
            private set

        fun of(side: Int): Square {
            require(side > 0) { "side must be positive" }
            created++
            return Square(side)
        }
    }
}

fun main() {
    println(AppInfo.describe())
    val a = Square.of(3)
    val b = Square.of(4)
    println("${a.area} ${b.area}")
    println("squares created: ${Square.created}")
}
