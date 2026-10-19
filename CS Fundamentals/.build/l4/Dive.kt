var depth = 0

fun dive() {
    depth++
    dive()          // no base case: every call pushes another frame
}

fun main() {
    try {
        dive()
    } catch (e: StackOverflowError) {
        println("stack overflowed at depth $depth")
    }
}
