fun <T> castList(xs: List<*>): List<T> = xs as List<T>    // the compiler can't check this

fun main() {
    val strings: List<String> = castList(listOf(1, 2))      // no error here...
    println("size ${strings.size}")                         // ...or here...
    val first: String = strings[0]                          // ...it blows up HERE
    println(first)
}
