// A function type: takes an Int, returns an Int
val double: (Int) -> Int = { x -> x * 2 }
val square: (Int) -> Int = { it * it }          // one parameter: call it `it`
val greet: () -> Unit = { println("hello") }    // no parameters, no result
var onDone: ((String) -> Unit)? = null          // nullable function type

// A higher-order function: it takes a function as a parameter
fun applyTwice(f: (Int) -> Int, x: Int): Int = f(f(x))

// Function as the LAST parameter: callers can use a trailing lambda
fun retry(times: Int, block: (Int) -> Boolean): Boolean {
    for (attempt in 1..times) {
        if (block(attempt)) return true
    }
    return false
}

fun main() {
    println(double(5))                    // call it like a function
    println(applyTwice(double, 3))        // pass it like a value
    println(applyTwice({ it + 10 }, 1))   // or pass a lambda directly
    greet()
    onDone?.invoke("ignored")             // null, so nothing happens
    onDone = { msg -> println("done: $msg") }
    onDone?.invoke("saved")

    val ok = retry(3) { attempt ->        // trailing lambda, outside ( )
        println("attempt $attempt")
        attempt == 2
    }
    println("succeeded=$ok")
}
