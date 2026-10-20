// Returns a NEW function: first f, then g
fun compose(f: (Int) -> Int, g: (Int) -> Int): (Int) -> Int = { x -> g(f(x)) }

// Returns a function that applies f n times
fun repeated(n: Int, f: (Int) -> Int): (Int) -> Int = { x ->
    var result = x
    repeat(n) { result = f(result) }
    result
}

fun addOne(x: Int) = x + 1

fun main() {
    val double: (Int) -> Int = { it * 2 }
    val doubleThenAdd = compose(double, ::addOne)
    val addThenDouble = compose(::addOne, double)
    println(doubleThenAdd(5))
    println(addThenDouble(5))
    println(repeated(3, double)(1))
    println(repeated(0, double)(7))
}
