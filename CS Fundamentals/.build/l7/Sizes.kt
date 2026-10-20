enum class Size(val ml: Int) {
    SMALL(250),
    MEDIUM(350),
    LARGE(450);                       // semicolon: members follow

    fun upsize(): Size = if (this == LARGE) LARGE else entries[ordinal + 1]
}

fun price(size: Size): Int = when (size) {   // exhaustive: no else needed
    Size.SMALL -> 300
    Size.MEDIUM -> 350
    Size.LARGE -> 400
}

fun main() {
    for (s in Size.entries) println("${s.name} #${s.ordinal}: ${s.ml} ml, ${price(s)} cents")
    println(Size.SMALL.upsize())
    println(Size.LARGE.upsize())
    println(Size.valueOf("MEDIUM").ml)
    try {
        Size.valueOf("medium")                 // names are case-sensitive
    } catch (e: IllegalArgumentException) {
        println(e.message)
    }
    println(Size.MEDIUM == Size.valueOf("MEDIUM"))   // one object per constant
}
