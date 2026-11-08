fun size(cm: Int): String = when (cm) {
    in 0..100 -> "small"
    in 50..150 -> "medium"
    else -> "large"
}

fun main() {
    println(size(75) + " " + size(100) + " " + size(120))
}
