interface Source<out T> {               // T only comes OUT (return types)
    fun next(): T
}

interface Sink<in T> {                  // T only goes IN (parameters)
    fun put(item: T)
}

class Counter(private var n: Int) : Source<Int> {
    override fun next(): Int = n++
}

class Printer : Sink<Any> {
    override fun put(item: Any) = println("printing $item")
}

fun main() {
    val ints: Source<Int> = Counter(10)
    val nums: Source<Number> = ints              // OK: out makes Source covariant
    println(nums.next())

    val anything: Sink<Any> = Printer()
    val strings: Sink<String> = anything         // OK: in makes Sink contravariant
    strings.put("hello")

    val byNumber: Comparable<Number> = object : Comparable<Number> {
        override fun compareTo(other: Number) = 0
    }
    val byDouble: Comparable<Double> = byNumber  // the stdlib's Comparable<in T>
    println(byDouble.compareTo(1.5))
}
